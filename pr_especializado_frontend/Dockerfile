# ---------- BUILD STAGE ----------
FROM node:20-alpine AS build-stage

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
ARG configuration=production
RUN npm run build -- --output-path=./dist/modulo-especializado-v2/ --configuration $configuration


# ---------- PRODUCTION STAGE ----------
FROM nginx:stable-alpine

ENV TZ=America/Lima

# Copiar build Angular
COPY --from=build-stage /app/dist/modulo-especializado-v2/browser /usr/share/nginx/html

# Copiar conf nginx
COPY ./nginx-custom.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
