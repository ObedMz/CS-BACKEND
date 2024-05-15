# Steam Inventory API Gateway

<div align="center">
<h3>Flow Diagram</h3>
  <img src="https://i.ibb.co/ZBNXswR/d9e5a065-f6ad-42da-a4a2-cc4c3f7fc359.png"/>
</div>

---

## Description

This is an intermediary API intended to manage and manipulate data from Global Offensive (CSGO). The API provides protected endpoints for modifying this data, as well as public endpoints for querying filtered information previously stored in the database.

---

## Main Features

- **Technologies Used**:
  - <img src="https://img.icons8.com/color/20/000000/spring-logo.png"/> Spring Boot for implementing business logic and managing the application's lifecycle.
  - <img src="https://img.icons8.com/?size=24&id=4A3RQai2arsg&format=png"/> Spring Security for application security, ensuring user authentication and authorization through JWT.
  - <img src="https://img.icons8.com/color/20/000000/mongodb.png"/> MongoDB as a NoSQL database for persistent storage of inventory data.
  - <img src="https://img.icons8.com/dusk/20/000000/forward.png"/> Utilization of asynchronous processes with Completable Futures for efficient management of concurrent tasks.
  - <img src="https://img.icons8.com/?size=20&id=rHpveptSuwDz&format=png"/> JWT (JSON Web Tokens) for user authentication and secure token generation.
  - <img src="https://img.icons8.com/dusk/20/000000/lock.png"/> Protected routes requiring authentication to access, ensuring security of sensitive data.
  - <img src="https://img.icons8.com/color/20/000000/spring-logo.png"/> Implementation of initialization events through Spring Command Line Runner for performing setup operations and data loading on application startup.
  - <img src="https://img.icons8.com/office/20/000000/clock.png"/> Usage of Schedulers for scheduling and automatic execution of repetitive tasks at regular intervals such as loading and synchronization of data from the external API.
  - <img src="https://img.icons8.com/?size=20&id=58901&format=png"/> Integration with Jackson and Mappers for serialization and deserialization of raw data, facilitating database update from an external endpoint.

---

## Functionalities

- **Protected Endpoints**:
  - Allows modification of CSGO inventory data, ensuring that only authorized users can access this functionality.

- **Public Endpoints**:
  - Provides filtered data stored in the database, coming from the CSGO Steam API, publicly accessible for querying.

---

## <div style="display: flex;"> <img src="https://img.icons8.com/?size=30&id=rdKV2dee9wxd&format=png"/>Swagger Documentation </div>

API documentation is generated with Swagger.
You can access the documentation and test the endpoints [here](http://213.133.102.110:31030/swagger-ui/index.html).

---

## Usage Examples

To use the protected endpoints, authentication is required through the exchange of JWT tokens generated at <code>{url}/login</code>. Once authenticated, users can send HTTP requests with the necessary data to securely and efficiently modify CSGO user inventories.

---

## Installation and Configuration

#### Clone the repository from GitHub
```bash
git clone https://github.com/ObedMz/CS-BACKEND.git
cd CS-BACKEND
./gradlew bootRun
