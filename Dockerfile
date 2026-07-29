# Estágio 1: Build da aplicação usando Maven com JDK 21/25
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o código fonte e os arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Compila o projeto gerando o .jar e ignora os testes para agilizar o build
RUN mvn clean package -DskipTests

# Estágio 2: Execução do JAR
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o arquivo .jar gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que a aplicação vai escutar
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]