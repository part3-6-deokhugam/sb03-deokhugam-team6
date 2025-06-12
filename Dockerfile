FROM eclipse-temurin:17-jdk-noble AS build
WORKDIR /workspace/app

# Gradle 파일 복사
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Gradle 의존성 캐시 활용
RUN ./gradlew dependencies

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build -x test

# 실행 이미지
FROM eclipse-temurin:17-jre-noble

RUN apt-get update && apt-get install -y \
    tesseract-ocr=5.3.4-1build5 \
    tesseract-ocr-eng \
    tesseract-ocr-kor \
    tesseract-ocr-chi-tra \
    libtesseract-dev

WORKDIR /
VOLUME /tmp
COPY --from=build /workspace/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Djna.library.path=/usr/lib/aarch64-linux-gnu", "-jar", "/app.jar"]