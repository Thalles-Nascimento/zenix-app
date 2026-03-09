FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# VITE_OTEL_URL é injetado em build time pelo docker-compose
ARG VITE_OTEL_URL=http://localhost:4318
ENV VITE_OTEL_URL=$VITE_OTEL_URL

RUN npm run build

# Stage 2: Nginx serve o dist
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
