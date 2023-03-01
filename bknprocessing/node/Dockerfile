FROM amd64/gradle:7.4.1-jdk17 AS build-image
# FROM gradle:7.4.1-jdk17-alpine AS build-image

WORKDIR /app

COPY bknprocessing/node /app/bknprocessing/node
# COPY gradle/*.gradle.kts /app/gradle/
# COPY gradle/detekt-config.yml /app/gradle/
# COPY *.kts /app/
# COPY gradle.properties /app/

RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:17-jdk AS runtime-image

ENV TZ=UTC

EXPOSE 8080
COPY --from=build-image /app/bknprocessing/nodep/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar","app.jar"]
