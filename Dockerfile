FROM gradle:8.2.1-jdk17 AS builder
# 위 버전은 예시이며, 상황에 맞춰 적절한 Gradle+jdk17 버전 태그 사용

# 작업 디렉터리 설정
WORKDIR /app

# (선택) Gradle 캐시를 최대한 활용하려면 build.gradle, gradle-wrapper 등을 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
# 종속성 캐시를 받기 위해 dependency만 먼저 다운로드
RUN gradle dependencies || true

# 실제 프로젝트 전체 소스 복사
COPY . .

# Gradle 빌드 (테스트 스킵 가능: -x test)
RUN gradle clean build -x test

# -----------------------
# 2단계: 런타임 실행 스테이지
# -----------------------
FROM openjdk:17-jdk-slim AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]