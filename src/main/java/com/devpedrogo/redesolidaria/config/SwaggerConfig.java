package com.devpedrogo.redesolidaria.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                    .title("Rede Solidaria API")
                    .version("1.0.0")
                    .description("API REST para gestão de doações, solicitações e usuários da Rede Solidária.")
                    .contact(new Contact()
                        .name("Equipe Rede Solidária")
                        .email("contato@redesolidaria.com")))
                // Aplica a necessidade de autenticação globalmente no Swagger
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        // Define o formato do token (Bearer JWT)
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT gerado no endpoint de login para autenticar as requisições.")));
    }

    @Bean
    public CommandLineRunner initAdmin(IUsuarioRepository usuarioRepository, IAdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("pedro.admin@redesolidaria.org").isEmpty()) {
                AdminEntity admin = AdminEntity.builder()
                        .nome("Administrador Pedro")
                        .email("pedro.admin@redesolidaria.org")
                        .senha(passwordEncoder.encode("admin123"))
                        .endereco("Rua Das Flores, 445")
                        .telefone("89 98777-8888")
                        .build();

                        if (admin.getPerfis() == null) {
                            admin.setPerfis(new HashSet<>());
                        }

                        admin.getPerfis().add(Perfil.ROLE_ADMIN);

                adminRepository.save(admin);
                System.out.println("✅ Usuário Admin inicial criado com sucesso via Spring!");
            }
        };
    }
}

