package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private OperadorEntity operador;

    @BeforeEach
    void setUp() {
        operador = new OperadorEntity();
        operador.setId(1);
        operador.setEmail("pedro@email.com");
        operador.setSenha("123456");
    }

    @Test
    @DisplayName("Deve retornar UserDetails quando o usuário for encontrado pelo email")
    void deveRetornarUserDetailsQuandoUsuarioExistir() {
        // Arrange
        when(usuarioRepository.findByEmail("pedro@email.com"))
                .thenReturn(Optional.of(operador));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("pedro@email.com");

        // Assert
        assertNotNull(result);
        assertEquals("pedro@email.com", result.getUsername());
        verify(usuarioRepository).findByEmail("pedro@email.com");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        // Arrange
        when(usuarioRepository.findByEmail("emailInexistente@email.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("emailInexistente@email.com")
        );

        assertEquals("Usuário não encontrado!", exception.getMessage());
        verify(usuarioRepository).findByEmail("emailInexistente@email.com");
    }
}