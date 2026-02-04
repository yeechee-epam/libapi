# Library API (libapi)
[![codecov](https://codecov.io/gh/yeechee-epam/libapi/graph/badge.svg?token=B5RVO03AD3)](https://codecov.io/gh/yeechee-epam/libapi)

A Spring Boot app for CRUD operations of a library's books and authors.

## Environment
- Java 17 and above
- JDK
- Docker

## Setup & Running
1. Clone repo and set up application properties and application-dev.properties:
  ```bash
  git clone https://github.com/yeechee-epam/libapi.git 
  cd libapi/src/main/resources
  touch application.properties
  touch application-dev.properties
  cat <<EOF > application.properties
  spring.profiles.active=dev
  EOF
  cat <<EOF > application-dev.properties
  spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml

spring.application.name=libapi
server.port=8080
spring.datasource.username=admin
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:7543/libapidb


# Hibernate is disabled because Liquibase will manage schema
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
  EOF
  ```
2. Update liquibase configurations:
   - in local dev environment, change values of db.username and db.password in pom.xml:
   ```bash
	  <profile>
			<id>dev</id>
			<properties>
				<db.username><your_username>></db.username>
				<db.password><your_password></db.password>
				</properties>
	  </profile>
    ``` 
3. Start database:
   ```bash
   docker-compose up -d
4. Run application in your local environment:
    ```bash
   mvn spring-boot:run -Pdev
   
5. Access application at http://localhost:8080
6. To confirm that data has been populated:
   1. open a shell in your running PostgreSQL container:
      ```bash
      docker exec -it postgres bash
    2. Connect to PostgreSQL:
       ```bash
       psql -U <username> -d libapidb
       SELECT * FROM authors;
       SELECT * FROM books;
    3. Exit:
       ```bash
       \q
       exit


## API documentation
Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI Spec: http://localhost:8080/v3/api-docs

## Key endpoints
### Books
GET /books/{id} 
- return paginated list of authors (params = page num, size)
- each book shows a list of items - authorId, authorLink to author's page, authorName, bookId, and book name
- return 200, 400 or 404 

PUT /books/{id}
- Update book's authorName and/or book name
- frontend form will show authorname and book name
- user can choose to update authorname and/or book name
- return 200, 400 or 404

POST /books
- create new book (field: book name, author name)
- if author exists and book name is unique, server creates new book under existing author
- if author is new and book name is unique, server creates new author and new book under created author
- Does not allow duplicate name (aka book entry with duplicate name will return 409)

DELETE /books/{id}
- delete book via book id
- return  204, 400, or 404

### Authors
GET /authors 
- return paginated list of authors (params = page num, size)
- each author shows a list of items - books (each with bookLink, book id, and book name), id, authorName
- return 200, 400 or 404

POST /authors
- create author (with name) and return author details
- author name is case insensitive

GET /authors/{id}
- return details of an author - a paginated list of books (with bookLink, id, and book name) of that author, id, and author name
- return 200 or 404

DELETE /authors/{id}
- delete author by id
- return 204 (successfully deleted), 404 (not found), or 409 (author has books)

GET /authors/{id}/books
- Returns a paginated list of all books for a specific author (params = page num, size)
- return 200, 400, 0r 404

## Unit testing & Integration testing
The tests are run against an in-memory H2 database.
  ```bash
  mvn clean test -Pdev
  ```

We are using @TestContainers, @SpringBootTest, and @AutoConfigureRestTestClient:
- @TestContainers for containerizing temporary independent Postgres DB for each test run. Alongside application.properties which specifies Liquibase migration (like in production environment) for the container, we can truly test actual with actual parameters of our DB.
- @SpringBootTest for launching full Spring Boot application context, for end-to-end testing
- @AutoConfigureRestTestClient for auto-configuring REST clients, which is aware of random port and base URI by the test server, replicating true HTTP requests

## Data modelling decision
Primary key strategy is based on auto generation / auto incrementation of each record ID (`@GeneratedValue(strategy = GenerationType.IDENTITY)`).
This strategy is optimal in a multi-instance cluster because in the case of concurrent writes to same central database, the database uses internal locking and increment id to locked thread, thereby generating unique ID to each record.

Deletion restriction is used for deletion of author that has books. This prevents loss of book records and is in line with data integrity.
If a user is to delete an author, user must remove every book the author has. 

## System design considerations
### Database:
Consider the scenario: to handle thousands of inserts/second in a multi-instance database setup (e.g., PostgreSQL with Patroni) on a Kubernetes cluster, we should consider the trade-offs between different scaling strategies - replication, partitioning (within a single DB), sharding, and others to apply the optimal strategy.

In this application, sharding enables write scaling as it spreads write operations across multiple nodes (shards), increasing write throughput. Hence, it is well-suited for our application that serves users across multiple zones. It comes with some overhead, but it is viable for the horizontal scaling and load balancing.
As for the other strategies, replication improves read scalability but not write scalability. Partitioning might improve write throughput within 1 DB instance but not beyond the limits of 1 server.

For future consideration, we can consider a distributed DB (e.g., Cassandra) that applies a combination of above strategies.

### Authentication & Authorization:
There are a few different implementation strategies of authorization and authentication in full-stack applications - a frontend app that delegates authorization server, jwt creation, and resource server roles to the backend API, or a frontend SPA that utilizes external ID providers such as Auth0 to act as authorization server and backend API to act as resource server (verifying JWT and enforces authorization).
This project utilizes Auth0 as the authorization server for the Angular SPA frontend, and Spring Boot as the resource server. The backend leverages Spring Security libraries (such as SecurityContextHolder) to provide security context, verify JWT claims, and enforce authorization rules.

Types of users:
- visitor: no login/sign up allowed, can view /books, /books/:id, /authors/:id, /authors/:id/books
- admin: must be logged in, sign up is restricted to assigned persons, can do all CRUD operations as per registered role in Auth0 (no permissions specified within role)

## References:
### Integration testing:
- https://dev.to/mspilari/integration-tests-on-spring-boot-with-postgresql-and-testcontainers-4dpc
- https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
#### Liquibase integration with Spring Boot:
- https://github.com/code-with-bellsoft/liquibase-demo
- https://github.com/liquibase/liquibase-postgresql
- https://github.com/PheaSoy/spring-boot-liquibase/tree/2e0ff1abe331f1080c22866ea77ce1673bf6ea69

#### auth0:
- https://github.com/auth0-developer-hub/api_spring_java_hello-world/tree/basic-authorization
- https://auth0.com/blog/spring-boot-authorization-tutorial-secure-an-api-java/


