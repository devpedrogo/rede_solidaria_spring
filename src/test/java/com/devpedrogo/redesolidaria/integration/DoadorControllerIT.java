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
import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DoadorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IDoadorRepository doadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private DoadorEntity doadorExistente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra Admin e realiza login para obter o token JWT de autorização
        AdminEntity admin = new AdminEntity();
        admin.setNome("Admin Autenticado");
        admin.setTelefone("79998870000");
        admin.setEmail("admin.doador@email.com");
        admin.setEndereco("Rua Central, 100");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setStatus(StatusUsuario.ATIVO);
        admin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(admin);

        LoginRequestDto loginDto = new LoginRequestDto("admin.doador@email.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();

        // 2. Prepara um Doador no banco H2 para testes de busca, edição e inativação
        doadorExistente = new DoadorEntity();
        doadorExistente.setNome("Lucas Doador");
        doadorExistente.setTelefone("79988776655");
        doadorExistente.setEmail("lucas.doador@email.com");
        doadorExistente.setEndereco("Rua das Doações, 10");
        doadorExistente.setSenha(passwordEncoder.encode("123456"));
        doadorExistente.setStatus(StatusUsuario.ATIVO);
        doadorExistente.getPerfis().add(Perfil.ROLE_DOADOR);

        doadorExistente = doadorRepository.save(doadorExistente);
    }

    @Test
    @DisplayName("Deve cadastrar novo doador e retornar 201 Created")
    void deveCadastrarDoadorComSucesso() throws Exception {
        DoadorDto novoDoador = new DoadorDto();
        novoDoador.setNome("Mariana Doadora");
        novoDoador.setTelefone("79977665544");
        novoDoador.setEndereco("Avenida Brasil, 200");

        mockMvc.perform(post("/doadores")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoDoador)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Mariana Doadora"))
                .andExpect(jsonPath("$.telefone").value("79977665544"));
    }

    @Test
    @DisplayName("Deve atualizar doador existente e retornar 200 OK")
    void deveAtualizarDoadorComSucesso() throws Exception {
        DoadorDto dtoAtualizacao = new DoadorDto();
        dtoAtualizacao.setNome("Lucas Silva Doador");
        dtoAtualizacao.setTelefone(doadorExistente.getTelefone());
        dtoAtualizacao.setEndereco("Nova Rua de Atualizacao, 50");

        mockMvc.perform(put("/doadores/{id}", doadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lucas Silva Doador"))
                .andExpect(jsonPath("$.endereco").value("Nova Rua de Atualizacao, 50"));
    }

    @Test
    @DisplayName("Deve listar todos os doadores e retornar 200 OK")
    void deveListarDoadoresComSucesso() throws Exception {
        mockMvc.perform(get("/doadores")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Lucas Doador"));
    }

    @Test
    @DisplayName("Deve buscar doador por ID existente e retornar 200 OK")
    void deveBuscarDoadorPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/doadores/{id}", doadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doadorExistente.getId()))
                .andExpect(jsonPath("$.nome").value("Lucas Doador"));
    }

    @Test
    @DisplayName("Deve inativar doador por ID e retornar 200 OK")
    void deveInativarDoadorComSucesso() throws Exception {
        mockMvc.perform(delete("/doadores/{id}", doadorExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem token")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/doadores"))
                .andExpect(status().isUnauthorized());
    }
}