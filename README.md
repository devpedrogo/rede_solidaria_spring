# 🛡️ Rede Solidária — Backend API

API RESTful desenvolvida em **Java + Spring Boot** para a plataforma **Rede Solidária**. O sistema gerencia doadores, beneficiários, doações e solicitações para projetos sociais, contando com autenticação robusta via **JWT**, controle de acesso baseado em perfis (RBAC) e persistência em banco relacional PostgreSQL.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17+ / 21
- **Framework Principal:** Spring Boot 3
- **Segurança:** Spring Security + JWT (JSON Web Tokens)
- **Persistência de Dados:** Spring Data JPA / Hibernate
- **Banco de Dados:** PostgreSQL (Supabase / local)
- **Documentação:** OpenAPI 3.0 / Swagger UI (Springdoc)
- **Gerenciador de Dependências:** Maven
- **Hospedagem / Deploy:** Render (Docker Container)

---

## 🚀 Funcionalidades Principais

- 🔐 **Autenticação & Autorização:** Login com emissão de token JWT e validação via interceptores de segurança.
- 👥 **Gestão de Usuários e Perfis:** Diferenciação de permissões entre Administradores, Operadores e Usuários.
- 🤝 **Gestão Social:** CRUD de doadores, beneficiários, solicitações e controle de fluxo de doações.
- 📄 **Documentação Interativa:** Interface Swagger para testes e consumo dos endpoints da API.

---

## ⚙️ Variáveis de Ambiente e Configuração

Para rodar a aplicação localmente ou em produção, configure as variáveis de ambiente no arquivo application.properties (ou no painel do provedor de hospedagem):

server.port=8080

# Banco de Dados PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST>:<PORT>/<DATABASE>?prepareThreshold=0
SPRING_DATASOURCE_USERNAME=<SEU_USUARIO>
SPRING_DATASOURCE_PASSWORD=<SUA_SENHA>

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Segurança / JWT
jwt.secret=<SUA_CHAVE_SECRETA_JWT_MUITO_LONGA_E_SEGURA>
jwt.expiration=86400000

> **Nota de Arquitetura (PgBouncer / Supabase):** O parâmetro `?prepareThreshold=0` na URL de conexão do PostgreSQL é fundamental para desativar os Prepared Statements do driver do PostgreSQL no Java, evitando incompatibilidades de conexão (PSQLException) com o pooler de transações PgBouncer do Supabase.

---

## 🔒 Segurança e CORS

A aplicação possui um **CorsConfigurationSource** centralizado na classe SecurityConfiguration, autorizando requisições originadas pelo frontend e garantindo a liberação prévia das chamadas preflight (OPTIONS):

- **Métodos permitidos:** GET, POST, PUT, DELETE, OPTIONS, PATCH.
- **Cabeçalhos permitidos:** Authorization, Content-Type, X-Requested-With, Accept.

---

## 📖 Documentação da API (Swagger UI)

Com o servidor em execução, acesse os links para visualizar e testar as rotas da API:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec (JSON):** http://localhost:8080/v3/api-docs

---

## 💻 Como Rodar o Projeto Localmente

### Pré-requisitos
- **Java JDK 17** ou superior instalado
- **Maven** instalado (ou utilize o wrapper ./mvnw)
- Instância do **PostgreSQL** ativa

### Passos:

1. **Clonar o repositório:**
   git clone [https://github.com/devpedrogo/rede_solidaria_spring.git](https://github.com/devpedrogo/rede_solidaria_spring.git)
   cd rede-solidaria-backend

2. **Configurar o banco de dados:**
   Crie um banco de dados PostgreSQL local chamado redesolidaria e ajuste as credenciais no src/main/resources/application.properties.

3. **Compilar e rodar a aplicação:**
   # Com Maven instalado:
   mvn spring-boot:run

   # Ou usando o Maven Wrapper:
   ./mvnw spring-boot:run

4. A API estará pronta para receber requisições em http://localhost:8080.

---

## ☁️ Hospedagem e Cold Start (Render Free Tier)

O backend encontra-se hospedado no plano gratuito do **Render**. 

- Por padrão, serviços inativos por mais de 15 minutos entram em modo de sleep (hibernação).
- O primeiro acesso após um período de inatividade aciona um cold start no container Java, que pode levar de **30 a 50 segundos** para inicializar completamente. As chamadas subsequentes responderão normalmente com alto desempenho.

---

## 📜 Licença

Este projeto é de uso acadêmico e para portfólio de desenvolvimento de software.