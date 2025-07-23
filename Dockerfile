# ===================================================================
# ESTÁGIO 1: Build da aplicação com Maven
# ===================================================================
FROM maven:3.8-openjdk-17 AS build

# Declara o argumento para este estágio
ARG MODULE_PATH
WORKDIR /app

# Copia o projeto inteiro (necessário para projetos multi-módulo)
COPY . .

# Roda o build a partir da raiz, focando no módulo específico
RUN mvn clean install -pl ${MODULE_PATH} -am -DskipTests

# ===================================================================
# ESTÁGIO 2: Imagem final da aplicação
# ===================================================================
FROM eclipse-temurin:17-jre-jammy

# ---- A CORREÇÃO ESTÁ AQUI ----
# Redeclare o argumento para que ele fique disponível neste estágio.
ARG MODULE_PATH

WORKDIR /app

# Agora ${MODULE_PATH} terá o valor correto ('brawl-core' ou 'brawl-web')
# e o Docker encontrará o arquivo .jar no caminho certo.
COPY --from=build /app/${MODULE_PATH}/target/*.jar app.jar

# A porta exposta é genérica; o docker-compose fará o mapeamento correto.
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]