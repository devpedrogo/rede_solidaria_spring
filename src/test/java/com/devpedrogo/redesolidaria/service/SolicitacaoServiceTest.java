package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devpedrogo.redesolidaria.dto.SolicitacaoCreateDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoResponseDto;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;
import com.devpedrogo.redesolidaria.repository.ISolicitacaoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock
    private ISolicitacaoRepository solicitacaoRepository;

    @Mock
    private IItemDoacaoRepository itemRepository;

    @Mock
    private IBeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    @Captor
    private ArgumentCaptor<ItemDoacaoEntity> itemCaptor;

    @Captor
    private ArgumentCaptor<SolicitacaoEntity> solicitacaoCaptor;

    private ItemDoacaoEntity itemEntity;
    private BeneficiarioEntity beneficiarioEntity;
    private SolicitacaoEntity solicitacaoEntity;

    @BeforeEach
    void setUp() {
        itemEntity = ItemDoacaoEntity.builder()
                .nome("Cesta Básica")
                .quantidade(10)
                .status(StatusItem.DISPONIVEL)
                .build();
        itemEntity.setId(1);

        beneficiarioEntity = new BeneficiarioEntity();
        beneficiarioEntity.setId(1);
        beneficiarioEntity.setNome("Maria Silva");

        solicitacaoEntity = SolicitacaoEntity.builder()
                .item(itemEntity)
                .beneficiario(beneficiarioEntity)
                .quantidadeSolicitada(5)
                .justificativa("Necessidade de alimentos")
                .status(StatusSolicitacao.PENDENTE)
                .build();
        solicitacaoEntity.setId(100);
    }

    @Nested
    @DisplayName("Testes do método criarSolicitacao")
    class CriarSolicitacaoTests {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o item não for encontrado")
        void deveLancarExcecaoQuandoItemNaoEncontrado() {
            // Arrange
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 1, 5, "Justificativa");
            when(itemRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> solicitacaoService.criarSolicitacao(dto)
            );

            assertEquals("Item de doação não encontrado com o ID: 1", exception.getMessage());
            verify(itemRepository).findById(1);
            verify(solicitacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o item não estiver DISPONIVEL")
        void deveLancarExcecaoQuandoItemIndisponivel() {
            // Arrange
            itemEntity.setStatus(StatusItem.ESGOTADO);
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 1, 5, "Justificativa");
            when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> solicitacaoService.criarSolicitacao(dto)
            );

            assertEquals("Este item não está disponível para solicitação.", exception.getMessage());
            verify(solicitacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando a quantidade solicitada for maior que o estoque")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            // Arrange
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 1, 15, "Justificativa");
            when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> solicitacaoService.criarSolicitacao(dto)
            );

            assertEquals("Quantidade solicitada (15) é maior que o estoque disponível (10).", exception.getMessage());
            verify(solicitacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o beneficiário não for encontrado")
        void deveLancarExcecaoQuandoBeneficiarioNaoEncontrado() {
            // Arrange
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 99, 5, "Justificativa");
            when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
            when(beneficiarioRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> solicitacaoService.criarSolicitacao(dto)
            );

            assertEquals("Beneficiário não encontrado com o ID: 99", exception.getMessage());
            verify(solicitacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar solicitação com sucesso e abater estoque mantendo status DISPONIVEL")
        void deveCriarSolicitacaoComSucesso() {
            // Arrange
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 1, 4, "Apoio alimentar");
            when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));
            when(solicitacaoRepository.save(any(SolicitacaoEntity.class))).thenAnswer(invocation -> {
                SolicitacaoEntity entity = invocation.getArgument(0);
                entity.setId(100);
                return entity;
            });

            // Act
            SolicitacaoResponseDto resultado = solicitacaoService.criarSolicitacao(dto);

            // Assert
            assertNotNull(resultado);
            verify(itemRepository).save(itemCaptor.capture());
            ItemDoacaoEntity itemSalvo = itemCaptor.getValue();
            assertEquals(6, itemSalvo.getQuantidade()); // 10 - 4
            assertEquals(StatusItem.DISPONIVEL, itemSalvo.getStatus());

            verify(solicitacaoRepository).save(solicitacaoCaptor.capture());
            SolicitacaoEntity solicitacaoSalva = solicitacaoCaptor.getValue();
            assertEquals(StatusSolicitacao.PENDENTE, solicitacaoSalva.getStatus());
            assertEquals(4, solicitacaoSalva.getQuantidadeSolicitada());
        }

        @Test
        @DisplayName("Deve criar solicitação e mudar status do item para RESERVADO quando o estoque zerar")
        void deveMudarStatusDoItemParaReservadoQuandoEstoqueZerar() {
            // Arrange
            SolicitacaoCreateDto dto = new SolicitacaoCreateDto(1, 1, 10, "Apoio total");
            when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));
            when(solicitacaoRepository.save(any(SolicitacaoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            solicitacaoService.criarSolicitacao(dto);

            // Assert
            verify(itemRepository).save(itemCaptor.capture());
            ItemDoacaoEntity itemSalvo = itemCaptor.getValue();
            assertEquals(0, itemSalvo.getQuantidade());
            assertEquals(StatusItem.RESERVADO, itemSalvo.getStatus());
        }
    }

    @Nested
    @DisplayName("Testes do método alterarStatus")
    class AlterarStatusTests {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException se a solicitação não for encontrada")
        void deveLancarExcecaoQuandoSolicitacaoNaoEncontrada() {
            // Arrange
            when(solicitacaoRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> solicitacaoService.alterarStatus(99, StatusSolicitacao.APROVADA)
            );

            assertEquals("Solicitação não encontrada com ID: 99", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException se o novo status for igual ao status atual")
        void deveLancarExcecaoQuandoNovoStatusForIgualAoAtual() {
            // Arrange
            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> solicitacaoService.alterarStatus(100, StatusSolicitacao.PENDENTE)
            );

            assertEquals("A solicitação já está no status: PENDENTE", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar alterar status de solicitação CONCLUIDA")
        void deveLancarExcecaoQuandoSolicitacaoJaEstiverConcluida() {
            // Arrange
            solicitacaoEntity.setStatus(StatusSolicitacao.CONCLUIDA);
            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> solicitacaoService.alterarStatus(100, StatusSolicitacao.REJEITADA)
            );

            assertEquals("A solicitação de id [100] encontra-se CONCLUÍDA, não é possível alterar seu status.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar alterar status de solicitação REJEITADA")
        void deveLancarExcecaoQuandoSolicitacaoJaEstiverRejeitada() {
            // Arrange
            solicitacaoEntity.setStatus(StatusSolicitacao.REJEITADA);
            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> solicitacaoService.alterarStatus(100, StatusSolicitacao.APROVADA)
            );

            assertEquals("A solicitação de id [100] foi REJEITADA, não é possível alterar seu status.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve devolver os itens ao estoque ao REJEITAR uma solicitação")
        void deveDevolverItensAoEstoqueAoRejeitar() {
            // Arrange
            itemEntity.setQuantidade(5); // Estoque atual
            solicitacaoEntity.setQuantidadeSolicitada(5);

            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act
            SolicitacaoResponseDto resultado = solicitacaoService.alterarStatus(100, StatusSolicitacao.REJEITADA);

            // Assert
            assertNotNull(resultado);
            assertEquals(StatusSolicitacao.REJEITADA, solicitacaoEntity.getStatus());

            verify(itemRepository).save(itemCaptor.capture());
            ItemDoacaoEntity itemSalvo = itemCaptor.getValue();
            assertEquals(10, itemSalvo.getQuantidade()); // 5 + 5 devolvidos

            verify(solicitacaoRepository).save(solicitacaoEntity);
        }

        @Test
        @DisplayName("Deve alterar status do item para ESGOTADO ao CONCLUIR e a quantidade do item for zero")
        void deveAlterarStatusDoItemParaEsgotadoAoConcluirComEstoqueZerado() {
            // Arrange
            itemEntity.setQuantidade(0); // Estoque zerado
            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act
            SolicitacaoResponseDto resultado = solicitacaoService.alterarStatus(100, StatusSolicitacao.CONCLUIDA);

            // Assert
            assertNotNull(resultado);
            assertEquals(StatusSolicitacao.CONCLUIDA, solicitacaoEntity.getStatus());
            assertEquals(StatusItem.ESGOTADO, itemEntity.getStatus());
            verify(solicitacaoRepository).save(solicitacaoEntity);
        }
    }

    @Nested
    @DisplayName("Testes do método listarSolicitacoes")
    class ListarSolicitacoesTests {

        @Test
        @DisplayName("Deve retornar lista de solicitações quando existirem registros")
        void deveRetornarListaDeSolicitacoes() {
            // Arrange
            when(solicitacaoRepository.findAll()).thenReturn(List.of(solicitacaoEntity));

            // Act
            List<SolicitacaoResponseDto> resultado = solicitacaoService.listarSolicitacoes();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(solicitacaoRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem solicitações")
        void deveRetornarListaVazia() {
            // Arrange
            when(solicitacaoRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<SolicitacaoResponseDto> resultado = solicitacaoService.listarSolicitacoes();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(solicitacaoRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método buscarPorId")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar SolicitacaoResponseDto quando o ID existir")
        void deveRetornarSolicitacaoQuandoIdExistir() {
            // Arrange
            when(solicitacaoRepository.findById(100)).thenReturn(Optional.of(solicitacaoEntity));

            // Act
            SolicitacaoResponseDto resultado = solicitacaoService.buscarPorId(100);

            // Assert
            assertNotNull(resultado);
            verify(solicitacaoRepository).findById(100);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(solicitacaoRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> solicitacaoService.buscarPorId(99)
            );

            assertEquals("Solicitação não encontrada com ID: 99", exception.getMessage());
            verify(solicitacaoRepository).findById(99);
        }
    }
}