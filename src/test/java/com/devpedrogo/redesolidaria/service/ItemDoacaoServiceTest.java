package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.devpedrogo.redesolidaria.dto.ItemDoacaoDto;
import com.devpedrogo.redesolidaria.dto.ItemDoacaoResponseDto;
import com.devpedrogo.redesolidaria.dto.ItemFiltroDto;
import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.DoacaoEfetivadaEntity;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.repository.IDoacaoEfetivadaRepository;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ItemDoacaoServiceTest {

    @Mock
    private IItemDoacaoRepository itemDoacaoRepository;

    @Mock
    private IDoacaoEfetivadaRepository doacaoEfetivadaRepository;

    @Mock
    private IDoadorRepository doadorRepository;

    @InjectMocks
    private ItemDoacaoService itemDoacaoService;

    @Captor
    private ArgumentCaptor<ItemDoacaoEntity> itemDoacaoCaptor;

    @Captor
    private ArgumentCaptor<DoacaoEfetivadaEntity> doacaoEfetivadaCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private DoadorEntity doadorEntity;
    private ItemDoacaoEntity itemExistente;
    private ItemDoacaoDto itemDoacaoDto;

    @BeforeEach
    void setUp() {
        doadorEntity = new DoadorEntity();
        doadorEntity.setId(1);
        doadorEntity.setNome("João Silva");

        itemExistente = ItemDoacaoEntity.builder()
                .nome("Arroz 5kg")
                .categoria(CategoriaItem.ALIMENTACAO)
                .quantidade(10)
                .status(StatusItem.DISPONIVEL)
                .build();
        itemExistente.setId(10);

        itemDoacaoDto = new ItemDoacaoDto();
        itemDoacaoDto.setDoadorId(1);
        itemDoacaoDto.setNome("Arroz 5kg");
        itemDoacaoDto.setCategoria(CategoriaItem.ALIMENTACAO);
        itemDoacaoDto.setQuantidadeDoada(5);
    }

    @Nested
    @DisplayName("Testes do método receberDoacao")
    class ReceberDoacaoTests {

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o doador não for encontrado")
        void deveLancarExcecaoQuandoDoadorNaoEncontrado() {
            // Arrange
            when(doadorRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> itemDoacaoService.receberDoacao(itemDoacaoDto)
            );

            assertEquals("Doador não encontrado com o ID: 1", exception.getMessage());
            verify(doadorRepository).findById(1);
            verify(itemDoacaoRepository, never()).findByNomeIgnoreCaseAndCategoria(any(), any());
            verify(itemDoacaoRepository, never()).save(any());
            verify(doacaoEfetivadaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve incrementar a quantidade no estoque existente e criar histórico de doação")
        void deveIncrementarEstoqueExistenteECriarHistorico() {
            // Arrange
            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));
            when(itemDoacaoRepository.findByNomeIgnoreCaseAndCategoria("Arroz 5kg", CategoriaItem.ALIMENTACAO))
                    .thenReturn(Optional.of(itemExistente));

            when(itemDoacaoRepository.save(any(ItemDoacaoEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            itemDoacaoService.receberDoacao(itemDoacaoDto);

            // Assert
            verify(itemDoacaoRepository).save(itemDoacaoCaptor.capture());
            ItemDoacaoEntity itemSalvo = itemDoacaoCaptor.getValue();
            assertEquals(15, itemSalvo.getQuantidade()); // 10 anteriores + 5 doados
            assertNotNull(itemSalvo.getDataAtualizacao());

            verify(doacaoEfetivadaRepository).save(doacaoEfetivadaCaptor.capture());
            DoacaoEfetivadaEntity historicoSalvo = doacaoEfetivadaCaptor.getValue();
            assertEquals(itemSalvo, historicoSalvo.getItem());
            assertEquals(doadorEntity, historicoSalvo.getDoador());
            assertEquals(5, historicoSalvo.getQuantidade());
            assertEquals("Entrada de estoque via API.", historicoSalvo.getObservacoes());
            assertNotNull(historicoSalvo.getData());
        }

        @Test
        @DisplayName("Deve alterar status de ESGOTADO para DISPONIVEL ao receber nova doação")
        void deveAlterarStatusDeEsgotadoParaDisponivel() {
            // Arrange
            itemExistente.setStatus(StatusItem.ESGOTADO);
            itemExistente.setQuantidade(0);

            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));
            when(itemDoacaoRepository.findByNomeIgnoreCaseAndCategoria("Arroz 5kg", CategoriaItem.ALIMENTACAO))
                    .thenReturn(Optional.of(itemExistente));
            when(itemDoacaoRepository.save(any(ItemDoacaoEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            itemDoacaoService.receberDoacao(itemDoacaoDto);

            // Assert
            verify(itemDoacaoRepository).save(itemDoacaoCaptor.capture());
            ItemDoacaoEntity itemSalvo = itemDoacaoCaptor.getValue();
            assertEquals(StatusItem.DISPONIVEL, itemSalvo.getStatus());
            assertEquals(5, itemSalvo.getQuantidade());
        }

        @Test
        @DisplayName("Deve criar um novo item de doação quando ele não existir no estoque")
        void deveCriarNovoItemSeNaoExistirNoEstoque() {
            // Arrange
            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));
            when(itemDoacaoRepository.findByNomeIgnoreCaseAndCategoria("Arroz 5kg", CategoriaItem.ALIMENTACAO))
                    .thenReturn(Optional.empty());
            when(itemDoacaoRepository.save(any(ItemDoacaoEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            itemDoacaoService.receberDoacao(itemDoacaoDto);

            // Assert
            verify(itemDoacaoRepository).save(itemDoacaoCaptor.capture());
            ItemDoacaoEntity novoItem = itemDoacaoCaptor.getValue();
            assertEquals("Arroz 5kg", novoItem.getNome());
            assertEquals(CategoriaItem.ALIMENTACAO, novoItem.getCategoria());
            assertEquals(5, novoItem.getQuantidade()); // 0 inicial + 5 doados
            assertEquals(StatusItem.DISPONIVEL, novoItem.getStatus());

            verify(doacaoEfetivadaRepository).save(any(DoacaoEfetivadaEntity.class));
        }
    }

    @Nested
    @DisplayName("Testes do método buscarComFiltro")
    class BuscarComFiltroTests {

        @Test
        @DisplayName("Deve buscar itens mantendo a ordenação solicitada quando os campos forem permitidos")
        void deveBuscarComOrdenacaoValida() {
            // Arrange
            ItemFiltroDto filtro = new ItemFiltroDto(null, null);
            Pageable pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());

            Page<ItemDoacaoEntity> pageRetorno = new PageImpl<>(List.of(itemExistente));

            when(itemDoacaoRepository.findAll(ArgumentMatchers.<Specification<ItemDoacaoEntity>>any(), any(Pageable.class)))
                    .thenReturn(pageRetorno);

            // Act
            Page<ItemDoacaoResponseDto> resultado = itemDoacaoService.buscarComFiltro(filtro, pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());

            verify(itemDoacaoRepository).findAll(ArgumentMatchers.<Specification<ItemDoacaoEntity>>any(), pageableCaptor.capture());
            Pageable pageableAplicado = pageableCaptor.getValue();
            assertEquals("nome: ASC", pageableAplicado.getSort().toString());
        }

        @Test
        @DisplayName("Deve forçar a ordenação por ID descrescente quando houver campo de ordenação proibido")
        void deveForcarOrdenacaoPorIdQuandoCampoForInvalido() {
            // Arrange
            ItemFiltroDto filtro = new ItemFiltroDto(null, null);
            // Campo 'quantidade' ou atributo inexistente/não permitido
            Pageable pageableInvalido = PageRequest.of(0, 10, Sort.by("campoProibido").ascending());

            Page<ItemDoacaoEntity> pageRetorno = new PageImpl<>(List.of(itemExistente));

            when(itemDoacaoRepository.findAll(ArgumentMatchers.<Specification<ItemDoacaoEntity>>any(), any(Pageable.class)))
                    .thenReturn(pageRetorno);

            // Act
            Page<ItemDoacaoResponseDto> resultado = itemDoacaoService.buscarComFiltro(filtro, pageableInvalido);

            // Assert
            assertNotNull(resultado);
            verify(itemDoacaoRepository).findAll(ArgumentMatchers.<Specification<ItemDoacaoEntity>>any(), pageableCaptor.capture());

            Pageable pageableAplicado = pageableCaptor.getValue();
            assertEquals("id: DESC", pageableAplicado.getSort().toString());
            assertEquals(0, pageableAplicado.getPageNumber());
            assertEquals(10, pageableAplicado.getPageSize());
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar ItemDoacaoResponseDto quando o ID for encontrado")
        void deveRetornarItemQuandoIdExistir() {
            // Arrange
            when(itemDoacaoRepository.findById(10)).thenReturn(Optional.of(itemExistente));

            // Act
            ItemDoacaoResponseDto resultado = itemDoacaoService.listarPorId(10);

            // Assert
            assertNotNull(resultado);
            verify(itemDoacaoRepository).findById(10);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(itemDoacaoRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> itemDoacaoService.listarPorId(99)
            );

            assertEquals("Item com id [99] não encontrado", exception.getMessage());
            verify(itemDoacaoRepository).findById(99);
        }
    }
}