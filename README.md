# Library API (libapi)
[![codecov](https://codecov.io/gh/yeechee-epam/libapi/graph/badge.svg?token=B5RVO03AD3)](https://codecov.io/gh/yeechee-epam/libapi)

A Spring Boot app for CRUD operations of a library's books and authors.

## Environment
- Java 17 and above
- JDK
- Docker

## Setup & Running
1. Clone repo and set up application properties:
  ```bash
  git clone git clone https://github.com/yeechee-epam/libapi.git 
  cd libapi/src/main/resources
  touch application.properties
  cat <<EOF > application.properties
  spring.application.name=libapi
  server.port=8080
  spring.datasource.username=USERNAME
  spring.datasource.password=PASSWORD
  spring.datasource.driver-class-name=org.postgresql.Driver
  spring.datasource.url=jdbc:postgresql://localhost:7543/libapidb

  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.format_sql=true
  spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
  EOF
  ```
2. Start database:
   ```bash
   docker-compose up -d
3. Open a shell in your running PostgreSQL container:
   ```bash
   docker exec -it postgres bash
4. Connect to PostgreSQL:
   ```bash
    psql -U <username>
5. Create database:
   ```bash
   CREATE DATABASE libapidb;
6. Connect to the database:
    ```bash
   \c libapidb
   
7. Insert sample data:
    ```bash
   INSERT INTO authors (name) VALUES ('Sample Author');
   INSERT INTO books (name, author_id) VALUES ('Sample Book', 1); 
8. Exit:
    ```bash
   \q
    exit
   
9. Run application in your local environment:
    ```bash
   mvn spring-boot:run
10. Access application at http://localhost:8080
   
   
To-do: use migration tool like Liquidbase in next iteration

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

## Unit testing
The unit tests are run against an in-memory H2 database.
  ```bash
  mvn clean test
  ```
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
