FROM eclipse-temurin:21-jdk-alpine AS build

# -------- FIX SSL --------
RUN apk update && apk add --no-cache ca-certificates openssl && update-ca-certificates

WORKDIR /app

# Copiamos primero los archivos necesarios para dependencias
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Aseguramos que mvnw tenga permisos y formato correcto
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Descargamos dependencias
RUN ./mvnw dependency:go-offline -B || echo "Dependencies downloaded."

# Ahora copiamos el resto del código
COPY . .

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Compilamos
RUN ./mvnw package -DskipTests

# ---------------- PRODUCCIÓN ----------------
FROM eclipse-temurin:21-jre-alpine AS production

RUN addgroup -S appgroup && adduser -S appuser -G appgroup -u 1100

RUN mkdir -p /actas
RUN chown -R appuser:appgroup /actas && chmod -R 770 /actas

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

COPY --from=build /app/target/consulta-op-backend-1.0.0-SNAPSHOT.jar /app/backend-cop.jar

USER appuser:appgroup

EXPOSE 80

CMD ["java", "-Dspring.profiles.active=production", "-jar", "backend-cop.jar"]
