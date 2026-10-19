package com.devpedrogo.redesolidaria.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Utiliza o application-test.yml com banco H2
@Transactional // Reseta as alterações no banco após cada teste
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Cadastra um usuário ATIVO para os testes de login
        OperadorEntity operadorAtivo = new OperadorEntity();
        operadorAtivo.setNome("Pedro Gouveia");
        operadorAtivo.setEmail("pedro@email.com");
        operadorAtivo.setSenha(passwordEncoder.encode("123456"));
        operadorAtivo.setStatus(StatusUsuario.ATIVO);
        operadorAtivo.getPerfis().add(Perfil.ROLE_OPERADOR);
        usuarioRepository.save(operadorAtivo);

        // Cadastra um usuário INATIVO para testar o bloqueio de login
        OperadorEntity operadorInativo = new OperadorEntity();
        operadorInativo.setNome("Usuário Inativo");
        operadorInativo.setEmail("inativo@email.com");
        operadorInativo.setSenha(passwordEncoder.encode("123456"));
        operadorInativo.setStatus(StatusUsuario.INATIVO);
        operadorInativo.getPerfis().add(Perfil.ROLE_OPERADOR);
        usuarioRepository.save(operadorInativo);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar 200 OK com token JWT")
    void deveAutenticarComSucesso() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("pedro@email.com", "123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("pedro@email.com"))
                .andExpect(jsonPath("$.nome").value("Pedro Gouveia"))
                .andExpect(jsonPath("$.role").value("ROLE_OPERADOR"))
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar logar com senha incorreta")
    void deveRetornarErroComSenhaIncorreta() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("pedro@email.com", "senha_errada");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar logar com e-mail inexistente")
    void deveRetornarErroComEmailInexistente() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("naoexiste@email.com", "123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve recusar o login de usuário INATIVO no sistema")
    void deveRecusarLoginUsuarioInativo() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("inativo@email.com", "123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }
}