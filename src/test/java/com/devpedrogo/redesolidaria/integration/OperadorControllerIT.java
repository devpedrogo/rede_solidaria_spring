package com.devpedrogo.redesolidaria.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorUpdateDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IOperadorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OperadorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IOperadorRepository operadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private OperadorEntity operadorExistente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra Admin para realizar o login e obter o Token
        AdminEntity admin = new AdminEntity();
        admin.setNome("Admin Autenticado");
        admin.setTelefone("79998870000");
        admin.setEmail("admin.auth@email.com");
        admin.setEndereco("Rua Central, 100");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setStatus(StatusUsuario.ATIVO);
        admin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(admin);

        LoginRequestDto loginDto = new LoginRequestDto("admin.auth@email.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();

        // 2. Cadastra um Operador padrão no H2 para os testes de atualização, busca e inativação
        operadorExistente = new OperadorEntity();
        operadorExistente.setNome("João Operador");
        operadorExistente.setTelefone("79998871122");
        operadorExistente.setEmail("joao.operador@email.com");
        operadorExistente.setEndereco("Rua dos Operadores, 45");
        operadorExistente.setSenha(passwordEncoder.encode("123456"));
        operadorExistente.setStatus(StatusUsuario.ATIVO);
        operadorExistente.getPerfis().add(Perfil.ROLE_OPERADOR);

        operadorExistente = operadorRepository.save(operadorExistente);
    }

    @Test
    @DisplayName("Deve cadastrar novo operador e retornar 201 Created")
    void deveCadastrarOperadorComSucesso() throws Exception {
        OperadorDto novoOperador = new OperadorDto();
        novoOperador.setNome("Maria Operadora");
        novoOperador.setTelefone("79998873344");
        novoOperador.setEmail("maria.operadora@email.com");
        novoOperador.setEndereco("Avenida Principal, 500");
        novoOperador.setSenha("123456");
        

        mockMvc.perform(post("/operadores")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoOperador)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Maria Operadora"))
                .andExpect(jsonPath("$.email").value("maria.operadora@email.com"));
    }

    @Test
    @DisplayName("Deve atualizar operador existente e retornar 200 OK")
    void deveAtualizarOperadorComSucesso() throws Exception {
        OperadorUpdateDto dtoAtualizacao = new OperadorUpdateDto(
            "João Silva Operador",
            operadorExistente.getTelefone(),
            "joao.silva@email.com",
            operadorExistente.getEndereco(),
            null
        );

        mockMvc.perform(put("/operadores/{id}", operadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Operador"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"));
    }

    @Test
    @DisplayName("Deve listar todos os operadores e retornar 200 OK")
    void deveListarOperadoresComSucesso() throws Exception {
        mockMvc.perform(get("/operadores")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("joao.operador@email.com"));
    }

    @Test
    @DisplayName("Deve buscar operador por ID existente e retornar 200 OK")
    void deveBuscarOperadorPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/operadores/{id}", operadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(operadorExistente.getId()))
                .andExpect(jsonPath("$.nome").value("João Operador"));
    }

    @Test
    @DisplayName("Deve inativar operador por ID e retornar 200 OK")
    void deveInativarOperadorComSucesso() throws Exception {
        mockMvc.perform(delete("/operadores/{id}", operadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem token")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/operadores"))
                .andExpect(status().isUnauthorized());
    }
}