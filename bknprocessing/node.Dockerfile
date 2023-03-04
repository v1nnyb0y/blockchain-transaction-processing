FROM gradle:7.4.1-jdk17 AS build-image
# FROM gradle:7.4.1-jdk17-alpine AS build-image

WORKDIR /bknd

COPY ./node /bknd/node
COPY ./common /bknd/common
COPY ./app /bknd/app

COPY ./gradle/*.gradle.kts /bknd/gradle/
COPY ./gradle/detekt-config.yml /bknd/gradle/

COPY ./*.kts /bknd/
COPY ./gradle.properties /bknd/

RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:17-jdk AS runtime-image

ENV TZ=UTC

EXPOSE 8080
COPY --from=build-image /bknd/node/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar","app.jar"]
