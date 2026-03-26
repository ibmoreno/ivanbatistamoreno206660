FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY backend-api/pom.xml backend-api/pom.xml
COPY worker/video-processing-worker/pom.xml worker/video-processing-worker/pom.xml
COPY worker/video-processing-worker/src worker/video-processing-worker/src

RUN chmod +x mvnw && ./mvnw -q -pl worker/video-processing-worker -am package -DskipTests
RUN mkdir -p worker/video-processing-worker/target/dependency && (cd worker/video-processing-worker/target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y ffmpeg \
    && rm -rf /var/lib/apt/lists/*
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/worker/video-processing-worker/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.album.worker.VideoProcessingWorkerApplication"]
