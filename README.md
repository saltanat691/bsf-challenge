# BSF Challenge
# Building
## Maven
```sh
$ ./mvnw clean package
```
## Docker
```sh
$ docker build -t {yourname}/bsf-challenge -f Dockerfile .
```
Here it is important, that the project root is your docker context (indicated by the . in the command).
{yourname} refers to your docker account name, and it is used to tag the image that's being built.
# Running
## Maven
```sh
$ ./mvnw clean spring-boot:run
```
## Docker
Very helpful in case if it's required to run it, for example, in k8s cluster.
```sh
$ docker run -p 8080:8080 {yourname}/bsf-challenge
```
## Swagger
Swagger documentation is available using this link
```sh
curl --request GET \
     --url 'http://localhost:8080/swagger-ui/index.html'
```
