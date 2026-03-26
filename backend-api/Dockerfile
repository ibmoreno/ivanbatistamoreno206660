FROM eclipse-temurin:21.0.2_13-jdk-alpine AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY backend-api/pom.xml backend-api/pom.xml
COPY worker/video-processing-worker/pom.xml worker/video-processing-worker/pom.xml
COPY backend-api/src backend-api/src

RUN chmod +x mvnw && ./mvnw -q -pl backend-api -am install -DskipTests
RUN mkdir -p backend-api/target/dependency && (cd backend-api/target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:21.0.2_13-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/backend-api/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.album.api.AlbumApiApplication"]
