FROM openjdk:21-slim
COPY ./target/*.jar /app/products-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/products-service.jar"]