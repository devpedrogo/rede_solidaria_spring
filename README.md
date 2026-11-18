# 🛡️ Rede Solidária — Backend API

API RESTful desenvolvida em **Java 17 & Spring Boot 3** para gestão de doações e projetos sociais. A aplicação conta com controle de acesso por perfis (RBAC), autenticação via JWT, banco PostgreSQL via Supabase e suporte a conteinerização com Docker.

---

## 🌐 Links Rápidos & Live Demo

- **Backend (Render):** [https://rede-solidaria-api.onrender.com](https://rede-solidaria-api.onrender.com)
- **Documentação Swagger UI:** [https://rede-solidaria-api.onrender.com/swagger-ui/index.html](https://rede-solidaria-api.onrender.com/swagger-ui/index.html)
- **Frontend App (Vercel):** [https://rede-solidaria-frontend.vercel.app](https://rede-solidaria-frontend.vercel.app)

> ⚡ **Nota sobre o Deploy no Render (Free Tier):** Devido ao *cold start* da hospedagem gratuita, a primeira requisição após inatividade pode levar entre 30 e 50 segundos para inicializar o container. As requisições seguintes possuem resposta imediata.

---

## 🛠️ Tech Stack & Arquitetura

- **Core:** Java 17, Spring Boot 3, Maven
- **Segurança:** Spring Security, JWT (JSON Web Token), RBAC
- **Persistência:** Spring Data JPA, Hibernate, PostgreSQL (Supabase / PgBouncer)
- **Testes & Qualidade:** JUnit 5, MockMvc, H2 Database (Testes de Integração)
- **DevOps & Deploy:** Docker, Dockerfile, Render, Vercel (Front)
- **Documentação:** OpenAPI 3.0 / Swagger UI (Springdoc)

---

## 🔑 Funcionalidades Chave

- 🔐 **Autenticação & RBAC:** Emissão e validação de tokens JWT com restrições por perfil (Admin, Operador, Doador e Beneficiário).
- 🤝 **Gestão Social:** CRUDs e regras de negócio para administradores, operadores, doadores e beneficiários.
- 📦 **Estoque Global de Doações:** Controle centralizado com incremento/decremento dinâmico de itens e auditoria de histórico.
- 📋 **Fluxo de Solicitações:** Sistema de pedidos com validações de prioridade, status e vínculo de beneficiários.

---

## ⚙️ Variáveis de Ambiente

Crie o arquivo `application.properties` ou defina as variáveis no seu ambiente de execução:

```properties
server.port=8080

# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST>:<PORT>/<DATABASE>?prepareThreshold=0
SPRING_DATASOURCE_USERNAME=<USUARIO>
SPRING_DATASOURCE_PASSWORD=<SENHA>

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update

# Segurança / JWT
jwt.secret=<CHAVE_SECRETA_JWT>
jwt.expiration=86400000
```

> **Dica de Infra:** O parâmetro `?prepareThreshold=0` na URL do PostgreSQL desativa Prepared Statements para evitar incompatibilidades de conexão com o pooler (PgBouncer) do Supabase.

---

## 🚀 Como Rodar Localmente

### Opção 1: Via Docker (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/devpedrogo/rede_solidaria_spring.git
cd rede_solidaria_spring

# Construa a imagem Docker
docker build -t rede-solidaria-api .

# Execute o container (defina as variáveis necessárias)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<HOST>:<PORT>/<DATABASE>?prepareThreshold=0" \
  -e SPRING_DATASOURCE_USERNAME="<USUARIO>" \
  -e SPRING_DATASOURCE_PASSWORD="<SENHA>" \
  -e JWT_SECRET="<SUA_CHAVE_SECRETA>" \
  rede-solidaria-api
```

### Opção 2: Via Maven & Java

```bash
# Compile e execute
./mvnw spring-boot:run
```

Acesse a API e a documentação em: `http://localhost:8080/swagger-ui/index.html`

---

## 📄 Licença

Projeto desenvolvido para fins acadêmicos e portfólio profissional.