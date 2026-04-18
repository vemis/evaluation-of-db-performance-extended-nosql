FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY microservice-couchbase-springdata-java/pom.xml microservice/pom.xml
COPY microservice-couchbase-springdata-java/src microservice/src

COPY common/pom.xml common/pom.xml
COPY common/src common/src

RUN mvn -f common/pom.xml clean install -DskipTests dependency:go-offline
RUN mvn -f microservice/pom.xml clean package -DskipTests dependency:go-offline

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends unzip && rm -rf /var/lib/apt/lists/*

COPY database/tpch-data-small.zip /tmp/tpch-data-small.zip
RUN unzip /tmp/tpch-data-small.zip -d /data && rm /tmp/tpch-data-small.zip

COPY --from=build /workspace/microservice/target/*.jar ./app.jar

EXPOSE 8087
ENTRYPOINT ["java", "-Xms512m", "-Xmx12g", "-jar", "app.jar"]
