# Estágio 1: Build da aplicação com Maven
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Estágio 2: Criação da imagem final com o Java Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copia o .jar gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que a aplicação vai usar (definida no docker-compose)
EXPOSE 8080
# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]