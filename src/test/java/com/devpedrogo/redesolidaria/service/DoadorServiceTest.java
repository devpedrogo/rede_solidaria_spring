package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

@ExtendWith(MockitoExtension.class)
public class DoadorServiceTest {

  @Mock
  IDoadorRepository doadorRepository;

  @InjectMocks
  DoadorService doadorService;

  @Captor
  private ArgumentCaptor<DoadorEntity> doadorCaptor;

  private DoadorDto doadorDto;

  @BeforeEach
  void setUp() {
    doadorDto = new DoadorDto();
    doadorDto.setNome("Maria Silva");
    doadorDto.setTelefone("79988887777");
    doadorDto.setEndereco("Rua das Flores, 100");
  }

  @Nested
  @DisplayName("Testes do método criarDoador")
  public class CadastrarDoadorTests {
    
    @Test
    @DisplayName("Deve criar um doador com sucesso e atribuir o perfil ROLE_DOADOR")
    void deveCriarDoadorComSucesso() throws Exception {
        // Arrange (Dado que...)
        when(doadorRepository.save(any(DoadorEntity.class))).thenAnswer(invocation -> {
            DoadorEntity entity = invocation.getArgument(0);
            entity.setId(1);
            return entity;
        });

        // Act (Quando...)
        DoadorResponseDto resultado = doadorService.criarDoador(doadorDto);

        // Assert (Então...)
        assertNotNull(resultado);

        // Verifica se o doadorRepository.save foi chamado e captura a entidade enviada
        verify(doadorRepository).save(doadorCaptor.capture());
        
        DoadorEntity doadorSalvo = doadorCaptor.getValue();
        assertEquals("Maria Silva", doadorSalvo.getNome());
        assertEquals("79988887777", doadorSalvo.getTelefone());
        assertEquals("Rua das Flores, 100", doadorSalvo.getEndereco());
        
        // Valida se a regra de negócio do perfil ROLE_DOADOR foi aplicada
        assertNotNull(doadorSalvo.getPerfis());
        System.out.println("Perfis: " + doadorSalvo.getPerfis());
        assertTrue(doadorSalvo.getPerfis().contains(Perfil.ROLE_DOADOR));
    }
  }
}
