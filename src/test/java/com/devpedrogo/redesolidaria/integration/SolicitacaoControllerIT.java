package com.devpedrogo.redesolidaria.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
import com.devpedrogo.redesolidaria.dto.AtualizarStatusSolicitacaoDto;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoCreateDto;
import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;
import com.devpedrogo.redesolidaria.repository.ISolicitacaoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SolicitacaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IBeneficiarioRepository beneficiarioRepository;

    @Autowired
    private IItemDoacaoRepository itemDoacaoRepository;

    @Autowired
    private ISolicitacaoRepository solicitacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private BeneficiarioEntity beneficiarioExistente;
    private ItemDoacaoEntity itemExistente;
    private SolicitacaoEntity solicitacaoExistente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra Admin para autenticação e obtém o token JWT
        AdminEntity admin = new AdminEntity();
        admin.setNome("Admin Solicitacoes");
        admin.setTelefone("79998870000");
        admin.setEmail("admin.solicitacao@email.com");
        admin.setEndereco("Rua Central, 100");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setStatus(StatusUsuario.ATIVO);
        admin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(admin);

        LoginRequestDto loginDto = new LoginRequestDto("admin.solicitacao@email.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();

        // 2. Persiste um Beneficiário
        beneficiarioExistente = new BeneficiarioEntity();
        beneficiarioExistente.setNome("Familia Silva");
        beneficiarioExistente.setTelefone("79988112244");
        beneficiarioExistente.setEmail("familia.silva@email.com");
        beneficiarioExistente.setEndereco("Rua do Sol, 12");
        beneficiarioExistente.setSenha(passwordEncoder.encode("123456"));
        beneficiarioExistente.setStatus(StatusUsuario.ATIVO);
        beneficiarioExistente.getPerfis().add(Perfil.ROLE_BENEFICIARIO);
        beneficiarioExistente.setTipoBeneficiario(TipoBeneficiario.FAMILIA);
        beneficiarioExistente.setNivelPrioridade(NivelPrioridade.CRITICA);
        beneficiarioExistente = beneficiarioRepository.save(beneficiarioExistente);

        // 3. Persiste um Item de Doação no Estoque
        itemExistente = new ItemDoacaoEntity();
        itemExistente.setNome("Cesta Básica Familiar");
        itemExistente.setCategoria(CategoriaItem.ALIMENTACAO);
        itemExistente.setQuantidade(20);
        itemExistente.setDataAtualizacao(LocalDateTime.now());
        itemExistente.setStatus(StatusItem.DISPONIVEL);
        itemExistente = itemDoacaoRepository.save(itemExistente);

        // 4. Persiste uma Solicitação para testes de consulta e alteração de status
        solicitacaoExistente = SolicitacaoEntity.builder()
                .beneficiario(beneficiarioExistente)
                .item(itemExistente)
                .quantidadeSolicitada(2)
                .justificativa("Familia sem renda no momento")
                .status(StatusSolicitacao.PENDENTE)
                .build();
        solicitacaoExistente = solicitacaoRepository.save(solicitacaoExistente);
    }

    @Test
    @DisplayName("Deve criar solicitação com sucesso e retornar 201 Created com cabeçalho Location")
    void deveCriarSolicitacaoComSucesso() throws Exception {
        SolicitacaoCreateDto dto = new SolicitacaoCreateDto(
            itemExistente.getId(),
            beneficiarioExistente.getId(),
            3,
            "Cesta básica para família com 4 pessoas"
        );

        mockMvc.perform(post("/solicitacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nomeItem").value("Cesta Básica Familiar"))
                .andExpect(jsonPath("$.nomeBeneficiario").value("Familia Silva"))
                .andExpect(jsonPath("$.quantidade").value(3))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve alterar status da solicitação para APROVADA e retornar 200 OK")
    void deveAlterarStatusComSucesso() throws Exception {
        AtualizarStatusSolicitacaoDto dto = new AtualizarStatusSolicitacaoDto(StatusSolicitacao.APROVADA);

        mockMvc.perform(patch("/solicitacoes/{id}/status", solicitacaoExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(solicitacaoExistente.getId()))
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("Deve listar todas as solicitações e retornar 200 OK")
    void deveListarSolicitacoesComSucesso() throws Exception {
        mockMvc.perform(get("/solicitacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nomeItem").value("Cesta Básica Familiar"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve buscar solicitação por ID existente e retornar 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/solicitacoes/{id}", solicitacaoExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(solicitacaoExistente.getId()))
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.justificativa").value("Familia sem renda no momento"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao criar solicitação com quantidade menor que 1")
    void deveValidarQuantidadeMinimaAoCriarSolicitacao() throws Exception {
        SolicitacaoCreateDto dtoInvalido = new SolicitacaoCreateDto(
            itemExistente.getId(),
            beneficiarioExistente.getId(),
            0, // Violacao do @Min(1)
            "Justificativa teste"
        );

        mockMvc.perform(post("/solicitacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem token")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/solicitacoes"))
                .andExpect(status().isUnauthorized());
    }
}