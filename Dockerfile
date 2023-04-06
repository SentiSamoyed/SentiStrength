FROM openjdk:17
ARG JAR_NAME

ENV DEBIAN_FRONTEND=noninteractive
ENV JAR_NAME=$JAR_NAME

EXPOSE 8080

WORKDIR /app

COPY ./build/libs/$JAR_NAME /app
COPY ./src/SentiStrength_Data /app/SentiStrength_Data

RUN if [ -z $JAR_NAME ]; then echo "Need to specify the JAR_NAME.";exit 1; fi;
RUN echo "Using the jar: '$JAR_NAME'"

ENTRYPOINT java -jar ./$JAR_NAME --web ./SentiStrength_Data