# ---------------- BUILD ----------------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiamos primero los archivos necesarios para dependencias
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Aseguramos que mvnw tenga permisos y formato correcto (por CRLF en Windows)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Descargamos dependencias (sin compilar aún)
RUN ./mvnw dependency:go-offline -B || echo "Dependencies cached."

# Ahora copiamos el resto del código
COPY . .

# Volvemos a asegurar el mvnw (por si se reescribió al copiar)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Compilamos sin tests
RUN ./mvnw clean package -DskipTests

# ---------------- PRODUCCIÓN ----------------
FROM eclipse-temurin:21-jre-alpine AS production

# Crear usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup -u 1100

ENV TZ=America/Lima

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

# Copiar el artefacto construido
COPY --from=build /app/target/consulta-op-cron-1.0.0-SNAPSHOT.jar /app/backend-cop.jar

USER appuser:appgroup

EXPOSE 80

# Comando de ejecución
CMD ["java", "-Dspring.profiles.active=production", "-jar", "backend-cop.jar"]
