FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 서브 모듈들의 build.gradle 파일 모두 복사 (캐싱 효율화)
COPY mobidic-api/build.gradle mobidic-api/
COPY mobidic-common/build.gradle mobidic-common/
COPY mobidic-domain/build.gradle mobidic-domain/
COPY mobidic-external/build.gradle mobidic-external/
COPY mobidic-storage/build.gradle mobidic-storage/

RUN ./gradlew --no-daemon dependencies

# 전체 소스 코드 복사
COPY . .
RUN ./gradlew --no-daemon :mobidic-api:bootJar -x test

# 2. Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 실행 모듈(api)의 JAR 파일만 추출하여 복사
COPY --from=builder /app/mobidic-api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
