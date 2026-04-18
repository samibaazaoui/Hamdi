FROM openjdk:17

COPY target/hamdi-0.0.1-SNAPSHOT.jar hamdi-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","hamdi-0.0.1-SNAPSHOT.jar"]
