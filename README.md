![picture](https://img.shields.io/badge/Java-11.0.1-green)
![picture](https://img.shields.io/badge/Spring%20Boot-2.1.1-green)
![picture](https://img.shields.io/badge/Flyway-5.2.4-green.svg)
![picture](https://img.shields.io/badge/Lombok-1.18.2-green.svg)
![picture](https://img.shields.io/badge/Jackson-2.9.8-green.svg)
![picture](https://img.shields.io/badge/Amache%20Commons-3.3.2-green)
![picture](https://img.shields.io/badge/aspectj-1.9.2-%23FF453C.svg)

![picture](https://img.shields.io/badge/REST-API-blueviolet)
![picture](https://img.shields.io/badge/Hibernate-5.3.7-blue.svg)
![picture](https://img.shields.io/badge/Docker-2-blue)
![picture](https://img.shields.io/badge/Swagger-2-blue)

## Overview

This project is an example of REST API based on Spring Boot 2. Project present example of City Transport network (e.g. underground, overground, trains network). 
 
The module has 2 main controllers, `ApiController` - for handling API communication 
and `OperatorController` which can be used for managing Operators.

`OperatorController` allows all requests from any domain _(COSRS is settled to "allow all")_, 
`ApiController` allows only "server to server" communication.

Module has DDL schema autoupdate settled to false and also 
[Flyway migration](https://flywaydb.org/) which should create all DB schema automatically.

See `src/main/resources/db/migration/V1.0__init.sql`

##### Api Communication flow
![picture](files/chart.png)

## Run on Docker

1. Build app image    

        rest-transport-api> mvn clean package docker:build

2. Go to /docker directory

        rest-transport-api> cd ./src/main/docker

3. Docker compose

        rest-transport-api/src/main/docker> docker-compose up

4. Test via Swagger [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    
5. Useful docker commands

        : go to cantainer and run bach
        docker exec -it rest-transport-api /bin/bash
        
        : stop all containers
        docker stop $(docker ps -a -q)
        
        : delete all containers
        docker rm $(docker ps -a -q)
        
        : delete all images 
        docker rmi $(docker images -q)
        
        : restart docker machine
        docker-machine restart default
        
        : get the IP address of machines
        docker-machine ip
        
    See more about Docker [https://www.docker.com/](https://www.docker.com/)

---

## Docs & examples
    
#### Swagger documentation & "try it out"

See API Documentation on Swagger [HOST/swagger-ui.html#/](http://localhost/swagger-ui.html#/)

*You can send any requests via any client such us `Postman` or `Insomnia` or just try it out 
via `SWAGGER` on project/host/swagger-ui.html*

Swagger is a popular API development tool which can help you to build and test APIs.

![picture](files/swagger.png)

---

#### REST API examples
[API request - response examples](api-json.md)


## Acknowledgments

#### Frameworks & Libs 

+ Spring (https://spring.io/projects/spring-boot)

+ Hibernate (http://hibernate.org/)

+ Jackson (https://github.com/FasterXML/jackson-databind)

+ AspectJ (https://www.eclipse.org/aspectj/)

+ Flyway (https://flywaydb.org/)

+ Apache Commons (https://commons.apache.org/)

+ Swagger (https://swagger.io/)

+ Docker (https://www.docker.com/)

+ Lombok (https://projectlombok.org/)

#### Used concepts

+ Naming Strategies (Snake & CammelCase) (https://docs.jboss.org/hibernate/orm/5.3/javadocs/org/hibernate/boot/model/naming/)

+ ThreadLocal (https://docs.oracle.com/javase/7/docs/api/java/lang/ThreadLocal.html)

+ Concurrency/Multithreading (https://docs.oracle.com/javase/tutorial/essential/concurrency/)

+ Spring Security (https://spring.io/projects/spring-security)

+ ResponseEntity (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html)

+ Fork - Join & RecursiveTask (https://www.baeldung.com/java-fork-join) (https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveTask.html)

+ Custom annotations (https://docs.oracle.com/javase/tutorial/java/annotations/)

+ Events in Spring (https://docs.spring.io/spring/docs/2.5.x/reference/beans.html#context-functionality-events)

+ Sprind Data, JPA Repositories (https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference)

+ Generics (https://docs.oracle.com/javase/tutorial/java/generics/index.html)

+ Java Streams & Optional (https://www.oracle.com/technical-resources/articles/java/ma14-java-se-8-streams.html) (https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html)

+ Models Projector (https://github.com/glaures/modelprojector)

+ Spring Validators (https://docs.spring.io/spring/docs/3.0.0.RC3/reference/html/ch05s07.html)

+ @Transactional in Spring (https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)

+ Content Negotiation in Spring (https://spring.io/blog/2013/05/11/content-negotiation-using-spring-mvc)

---

## See also

**Author** - Slawomir Hadas (https://github.com/hadasbro)

