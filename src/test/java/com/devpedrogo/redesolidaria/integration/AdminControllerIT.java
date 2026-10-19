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
import com.devpedrogo.redesolidaria.dto.AdminDto;
import com.devpedrogo.redesolidaria.dto.AdminUpdateDto;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AdminEntity adminExistente;
    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra um Admin com senha conhecida no banco H2
        adminExistente = new AdminEntity();
        adminExistente.setNome("Carlos Admin");
        adminExistente.setTelefone("79998870011");
        adminExistente.setEmail("carlos@admin.com");
        adminExistente.setEndereco("Rua das Flores, 123");
        adminExistente.setSenha(passwordEncoder.encode("123456"));
        adminExistente.setStatus(StatusUsuario.ATIVO);
        adminExistente.getPerfis().add(Perfil.ROLE_ADMIN);

        adminExistente = adminRepository.save(adminExistente);

        // 2. Realiza o login real via endpoint de autenticação
        LoginRequestDto loginDto = new LoginRequestDto("carlos@admin.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        // 3. Extrai o token do JSON utilizando a propriedade expiresIn/token
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();
    }

    @Test
    @DisplayName("Deve cadastrar novo administrador e retornar 201 Created")
    void deveCadastrarAdminComSucesso() throws Exception {
        AdminDto novoAdmin = new AdminDto(
            "Fernanda Admin",
            "79998870099",
            "fernanda@admin.com",
            "Rua Chica Da Silva",
            "123456"
        );

        mockMvc.perform(post("/admins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoAdmin)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Fernanda Admin"))
                .andExpect(jsonPath("$.email").value("fernanda@admin.com"));
    }

    @Test
    @DisplayName("Deve atualizar administrador existente e retornar 200 OK")
    void deveAtualizarAdminComSucesso() throws Exception {
        AdminUpdateDto dtoAtualizacao = new AdminUpdateDto(
            "Carlos Silva Admin",
            "79998870099",
            "carlos.silva@admin.com",
            "Rua Chica Da Silva",
            null
        );

        mockMvc.perform(put("/admins/{id}", adminExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Silva Admin"))
                .andExpect(jsonPath("$.email").value("carlos.silva@admin.com"));
    }

    @Test
    @DisplayName("Deve listar todos os administradores e retornar 200 OK")
    void deveListarAdminsComSucesso() throws Exception {
        mockMvc.perform(get("/admins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[1].email").value("carlos@admin.com"));
    }

    @Test
    @DisplayName("Deve buscar administrador por ID existente e retornar 200 OK")
    void deveListarAdminPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/admins/{id}", adminExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminExistente.getId()))
                .andExpect(jsonPath("$.nome").value("Carlos Admin"));
    }

    @Test
    @DisplayName("Deve inativar administrador por ID e retornar 200 OK")
    void deveInativarAdminComSucesso() throws Exception {
        // 1. Cria um SEGUNDO admin que será inativado
        AdminEntity adminParaInativar = new AdminEntity();
        adminParaInativar.setNome("Admin Secundario");
        adminParaInativar.setTelefone("79998870022");
        adminParaInativar.setEmail("secundario@admin.com");
        adminParaInativar.setEndereco("Rua Teste, 456");
        adminParaInativar.setSenha(passwordEncoder.encode("123456"));
        adminParaInativar.setStatus(StatusUsuario.ATIVO);
        adminParaInativar.getPerfis().add(Perfil.ROLE_ADMIN);
        adminParaInativar = adminRepository.save(adminParaInativar);

        // 2. O admin logado (Carlos) inativa o Admin Secundario
        mockMvc.perform(delete("/admins/{id}", adminParaInativar.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request e mensagem de limite ao tentar inativar admin mantendo apenas 2 ou menos ativos")
    void deveBloquearInativacaoQuandoTotalForIgualOuMenorQueDois() throws Exception {
        // Banco já tem 2 admins ativos (1º Inicial + 2º Carlos).
        // O id do 1º admin geralmente é 1.
        Integer idAdminInicial = 1; 

        mockMvc.perform(delete("/admins/{id}", idAdminInicial)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Operação negada: O sistema exige o mínimo de 2 administradores ativos para segurança. Cadastre outro administrador antes de inativar este."));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request e mensagem especifica ao tentar inativar a propria conta")
    void deveRetornarErroAoTentarInativarAPropriaConta() throws Exception {
        // 1. Cadastra o 3º admin para ter > 2 ativos no banco (1º Inicial + 2º Carlos + 3º Auxiliar)
        AdminEntity terceiroAdmin = new AdminEntity();
        terceiroAdmin.setNome("Terceiro Admin");
        terceiroAdmin.setEmail("terceiro@admin.com");
        terceiroAdmin.setTelefone("79998870033");
        terceiroAdmin.setEndereco("Rua Teste, 789");
        terceiroAdmin.setSenha(passwordEncoder.encode("123456"));
        terceiroAdmin.setStatus(StatusUsuario.ATIVO);
        terceiroAdmin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(terceiroAdmin);

        // 2. O Carlos tenta inativar a si próprio (adminExistente)
        mockMvc.perform(delete("/admins/{id}", adminExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Operação negada: Você não pode inativar a sua própria conta."));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/admins"))
                .andExpect(status().isUnauthorized());
    }
}