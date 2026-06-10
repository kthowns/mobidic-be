FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 서브 모듈들의 build.gradle 파일 모두 복사 (캐싱 효율화)
COPY app-api/build.gradle app-api/
COPY core-common/build.gradle core-common/
COPY core-domain/build.gradle core-domain/
COPY infra-external/build.gradle infra-external/
COPY infra-storage/build.gradle infra-storage/

RUN ./gradlew --no-daemon dependencies

# 전체 소스 코드 복사
COPY . .
RUN ./gradlew --no-daemon :app-api:bootJar -x test

# 2. Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 실행 모듈(api)의 JAR 파일만 추출하여 복사
COPY --from=builder /app/app-api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
