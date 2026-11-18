package com.devpedrogo.redesolidaria.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.devpedrogo.redesolidaria.dto.ItemDoacaoDto;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ItemDoacaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IDoadorRepository doadorRepository;

    @Autowired
    private IItemDoacaoRepository itemDoacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private DoadorEntity doadorExistente;
    private ItemDoacaoEntity itemExistente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Cadastra Admin e obtém Token JWT
        AdminEntity admin = new AdminEntity();
        admin.setNome("Admin Autenticado");
        admin.setTelefone("79998870000");
        admin.setEmail("admin.doacao@email.com");
        admin.setEndereco("Rua Central, 100");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setStatus(StatusUsuario.ATIVO);
        admin.getPerfis().add(Perfil.ROLE_ADMIN);
        adminRepository.save(admin);

        LoginRequestDto loginDto = new LoginRequestDto("admin.doacao@email.com", "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        tokenAdmin = jsonNode.get("token").asText();

        // 2. Persiste um Doador Válido
        doadorExistente = new DoadorEntity();
        doadorExistente.setNome("Doador Exemplo");
        doadorExistente.setTelefone("79988776655");
        doadorExistente.setEmail("doador.exemplo@email.com");
        doadorExistente.setEndereco("Rua do Doador, 50");
        doadorExistente.setSenha(passwordEncoder.encode("123456"));
        doadorExistente.setStatus(StatusUsuario.ATIVO);
        doadorExistente.getPerfis().add(Perfil.ROLE_DOADOR);
        doadorExistente = doadorRepository.save(doadorExistente);

        // 3. Persiste um Item de Doação no H2 para testes de busca
        itemExistente = new ItemDoacaoEntity();
        itemExistente.setNome("Cesta Básica");
        itemExistente.setCategoria(CategoriaItem.ALIMENTACAO); 
        itemExistente.setQuantidade(10);
        itemExistente.setDataAtualizacao(LocalDateTime.now());
        itemExistente.setStatus(StatusItem.DISPONIVEL);
        itemExistente = itemDoacaoRepository.save(itemExistente);
    }

    @Test
    @DisplayName("Deve cadastrar nova doação e retornar 201 Created sem corpo")
    void deveCriarItemDoacaoComSucesso() throws Exception {
        ItemDoacaoDto dto = new ItemDoacaoDto();
        dto.setNome("Agasalho de Frio");
        dto.setCategoria(CategoriaItem.VESTUARIO);
        dto.setQuantidadeDoada(5);
        dto.setDoadorId(doadorExistente.getId());

        mockMvc.perform(post("/doacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve listar itens doados paginados com filtro e retornar 200 OK")
    void deveListarItensComFiltrosComSucesso() throws Exception {
        mockMvc.perform(get("/doacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Cesta Básica"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("Deve buscar item doado por ID existente e retornar 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/doacoes/{id}", itemExistente.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cesta Básica"))
                .andExpect(jsonPath("$.quantidade").value(10));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar doação com quantidade menor que 1")
    void deveValidarQuantidadeMinimaAoCriarDoacao() throws Exception {
        ItemDoacaoDto dtoInvalido = new ItemDoacaoDto();
        dtoInvalido.setNome("Cesta Básica");
        dtoInvalido.setCategoria(CategoriaItem.ALIMENTACAO);
        dtoInvalido.setQuantidadeDoada(0); // Violação da anotação @Min(1)
        dtoInvalido.setDoadorId(doadorExistente.getId());

        mockMvc.perform(post("/doacoes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar acessar sem token")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/doacoes"))
                .andExpect(status().isUnauthorized());
    }
}