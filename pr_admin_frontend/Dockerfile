# Step 1: Build the angular application in a node environment
FROM node:20.9.0 AS  build-stage

WORKDIR /app
COPY package*.json /app/
RUN npm install
COPY ./ /app/
ARG configuration=production
RUN npm run build -- --output-path=./dist/pr-admin-v2/ --configuration $configuration


# Stage 2, based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:1.15
# Set timezone to America/Lima
ENV TZ=America/Lima
#Copy ci-dashboard-dist
COPY --from=build-stage /app/dist/pr-admin-v2/browser /usr/share/nginx/html
#Copy default nginx configuration
COPY ./nginx-custom.conf /etc/nginx/conf.d/default.conf

