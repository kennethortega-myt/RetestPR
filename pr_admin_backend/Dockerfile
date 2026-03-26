# ---------------- BUILD ----------------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar Maven wrapper y pom
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Corregir fin de línea y permisos
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Descargar dependencias offline
RUN ./mvnw dependency:go-offline -B || echo "Dependencies downloaded."

# Copiar el resto del proyecto
COPY . .

# Corregir permisos y compilar
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw package -DskipTests

# ---------------- PRODUCCIÓN ----------------
FROM eclipse-temurin:21-jre-alpine AS production

# Crear usuario no root
RUN adduser -D -u 1100 appuser

# Crear carpeta para archivos y asignar permisos
RUN mkdir -p /actas
RUN chown -R appuser:appuser /actas && chmod -R 770 /actas

# Configuración de zona horaria
ENV TZ=America/Lima

# Cache de fuentes (opcional, evita fallo en Alpine)
RUN fc-cache -f -v || true

WORKDIR /app

# 🔹 COPIAR CERTIFICADO
#COPY mongo-ca.pem /tmp/mongo-ca.pem

# Si hay varios certificados concatenados, keytool puede importarlos uno a uno usando un bucle
#RUN apk add --no-cache bash \
#    && csplit -sz /tmp/mongo-ca.pem '/-----BEGIN CERTIFICATE-----/' '{*}' \
#    && for cert in xx*; do \
#         keytool -import -trustcacerts -alias $(basename $cert) -file $cert -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt; \
#       done \
#    && rm xx*

# Copiar JAR desde build
COPY --from=build /app/target/pr-admin-backend-1.0.0-SNAPSHOT.jar /app/backend-pr.jar

# Usar usuario no root
USER appuser

EXPOSE 80

# Comando para iniciar la app
CMD ["java", "-Dspring.profiles.active=production", "-jar", "backend-pr.jar"]
