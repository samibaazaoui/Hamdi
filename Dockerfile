FROM eclipse-temurin:21-jdk

COPY com.inn.cafe/target/com.inn.cafe-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
