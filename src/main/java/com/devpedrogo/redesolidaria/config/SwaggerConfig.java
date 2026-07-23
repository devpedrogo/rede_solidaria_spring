package com.devpedrogo.redesolidaria.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                    .description("Sistema de gerenciamento de uma rede de doações."))
                // Aplica a necessidade de autenticação globalmente no Swagger
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        // Define o formato do token (Bearer JWT)
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
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

                adminRepository.save(admin);
                System.out.println("✅ Usuário Admin inicial criado com sucesso via Spring!");
            }
        };
    }
}

