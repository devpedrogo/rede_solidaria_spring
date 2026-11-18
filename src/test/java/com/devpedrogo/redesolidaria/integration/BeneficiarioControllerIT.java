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
import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BeneficiarioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IBeneficiarioRepository beneficiarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private BeneficiarioEntity beneficiarioExistente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra Admin e realiza login para obter o token JWT de autorização
        AdminEntity admin = new AdminEntity();
        admin.setNome("Admin Autenticado");
        admin.setTelefone("79998870000");
        admin.setEmail("admin.beneficiario@email.com");
        admin.setEndereco("Rua Central, 100");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setStatus(StatusUsuario.ATIVO);
        admin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(admin);

        LoginRequestDto loginDto = new LoginRequestDto("admin.beneficiario@email.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();

        // 2. Prepara um Beneficiário no banco H2 para testes de busca, edição e inativação
        beneficiarioExistente = new BeneficiarioEntity();
        beneficiarioExistente.setNome("Familia Santos");
        beneficiarioExistente.setTelefone("79988112233");
        beneficiarioExistente.setEmail("familia.santos@email.com");
        beneficiarioExistente.setEndereco("Rua do Acolhimento, 20");
        beneficiarioExistente.setSenha(passwordEncoder.encode("123456"));
        beneficiarioExistente.setStatus(StatusUsuario.ATIVO);
        beneficiarioExistente.getPerfis().add(Perfil.ROLE_BENEFICIARIO);
        beneficiarioExistente.setTipoBeneficiario(TipoBeneficiario.FAMILIA); 
        beneficiarioExistente.setNivelPrioridade(NivelPrioridade.CRITICA);   

        beneficiarioExistente = beneficiarioRepository.save(beneficiarioExistente);
    }

    @Test
    @DisplayName("Deve cadastrar novo beneficiário e retornar 201 Created")
    void deveCadastrarBeneficiarioComSucesso() throws Exception {
        BeneficiarioDto novoBeneficiario = new BeneficiarioDto();
        novoBeneficiario.setNome("Abrigo Esperança");
        novoBeneficiario.setTelefone("79977223344");
        novoBeneficiario.setEndereco("Avenida das Nações, 300");
        novoBeneficiario.setTipoBeneficiario(TipoBeneficiario.ONG); 
        novoBeneficiario.setNivelPrioridade(NivelPrioridade.MEDIA);        

        mockMvc.perform(post("/beneficiarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoBeneficiario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Abrigo Esperança"))
                .andExpect(jsonPath("$.telefone").value("79977223344"));
    }

    @Test
    @DisplayName("Deve atualizar beneficiário existente e retornar 200 OK")
    void deveAtualizarBeneficiarioComSucesso() throws Exception {
        BeneficiarioDto dtoAtualizacao = new BeneficiarioDto();
        dtoAtualizacao.setNome("Familia Santos Atualizada");
        dtoAtualizacao.setTelefone(beneficiarioExistente.getTelefone());
        dtoAtualizacao.setEndereco("Nova Rua do Acolhimento, 40");
        dtoAtualizacao.setTipoBeneficiario(beneficiarioExistente.getTipoBeneficiario());
        dtoAtualizacao.setNivelPrioridade(NivelPrioridade.URGENTE); // Ajuste conforme o seu Enum real

        mockMvc.perform(put("/beneficiarios/{id}", beneficiarioExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Familia Santos Atualizada"))
                .andExpect(jsonPath("$.endereco").value("Nova Rua do Acolhimento, 40"));
    }

    @Test
    @DisplayName("Deve listar todos os beneficiários e retornar 200 OK")
    void deveListarBeneficiariosComSucesso() throws Exception {
        mockMvc.perform(get("/beneficiarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Familia Santos"));
    }

    @Test
    @DisplayName("Deve buscar beneficiário por ID existente e retornar 200 OK")
    void deveBuscarBeneficiarioPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/beneficiarios/{id}", beneficiarioExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(beneficiarioExistente.getId()))
                .andExpect(jsonPath("$.nome").value("Familia Santos"));
    }

    @Test
    @DisplayName("Deve inativar beneficiário por ID e retornar 200 OK")
    void deveInativarBeneficiarioComSucesso() throws Exception {
        mockMvc.perform(delete("/beneficiarios/{id}", beneficiarioExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem token")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/beneficiarios"))
                .andExpect(status().isUnauthorized());
    }
}