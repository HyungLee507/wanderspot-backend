도커파일 백업
# Build 단계
FROM gradle:8.2.1-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
RUN gradle dependencies || true

COPY . .

RUN gradle clean build -x test

# 런타임 실행 단계
FROM openjdk:17-jdk-slim AS runner
WORKDIR /app

COPY --from=builder /app/build/libs/*.war app.war

# .env 파일에서 전달받은 환경변수를 런타임에서 사용
ARG ENV_FILE=/app/.env
COPY ${ENV_FILE} .env

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "source .env && java -jar /app/app.jar"]
