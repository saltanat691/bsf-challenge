FROM openjdk:11

ENV LANG=C.UTF-8

EXPOSE 8080

WORKDIR /service
COPY target/bsf-0.0.1-SNAPSHOT.jar service.jar

# Run the jar file
CMD java -jar service.jar