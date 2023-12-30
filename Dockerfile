FROM openjdk:17-oracle
COPY target/Online-1.0.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "Onliner.jar"]