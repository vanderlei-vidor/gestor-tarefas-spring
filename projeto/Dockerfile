# Usa uma imagem do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Cria uma pasta para o app
WORKDIR /app

# Copia os arquivos do projeto para dentro do Docker
COPY . .

# Dá permissão para o Maven Wrapper e compila o projeto
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Comando para rodar o sistema
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]