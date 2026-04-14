# Tax Gap Detection & Compliance Validation Service

## 📌 Project Overview

This application is designed to assist tax auditors in validating financial transactions, calculating expected tax, detecting tax gaps, applying compliance rules, and generating exception reports.

The system processes transaction batches, validates data, applies rule-based checks, stores results, and provides summarized reports for audit purposes.

---

## ⚙️ Technical Stack

* Java 17
* Spring Boot 3.x
* Spring Data JPA (Hibernate)
* MySQL (RDBMS)
* Spring Security
* JUnit 5 + Mockito
* EclEmma (Code Coverage in STS/Eclipse)

---

## 🏗️ Architecture

The application follows a **layered architecture**:

Controller → Service → Repository → Domain → Configuration

### Layers Explanation:

* **Controller** → Handles REST API requests
* **Service** → Contains business logic (validation, tax calculation, rule engine)
* **Repository** → Database operations using JPA
* **Entity** → Database models
* **DTO** → Request & Response data transfer objects
* **Configuration** → Security setup, application initialization (Spring Security, DataInitializer)
---

## 📦 DTO Layer (Important)

DTOs are used to separate internal entities from API communication.

### Request DTO

* `TransactionUploadRequest`

  * transactionId
  * date
  * customerId
  * amount
  * taxRate
  * reportedTax
  * transactionType

### Response DTOs

* `TransactionUploadResponse`
* `ExceptionResponseDto`
* `CustomerTaxSummaryDto`

DTOs ensure:

* Clean API structure
* No direct exposure of entities
* Better maintainability

---

## 🚀 Functional Requirements Implemented

### 1. Transaction Upload & Validation

* Upload batch transactions via API

* Validation rules:

  * Required fields validation
  * Amount must be greater than 0
  * Date format validation

* Stores:

  * Transaction data
  * Validation status (SUCCESS / FAILURE)
  * Failure reason

---

### 2. Tax Gap Calculation Engine

* expectedTax = amount × taxRate
* taxGap = expectedTax - reportedTax

### Compliance Logic:

* |taxGap| ≤ 1 → COMPLIANT
* taxGap > 1 → UNDERPAID
* taxGap < -1 → OVERPAID

---

### 3. Rule Engine

* Rule-based validation system implemented

Supports:

* High value transaction rule
* GST validation rule
* Refund validation rule

Rules can be:

* Enabled / Disabled
* Configured dynamically using JSON

---

### 4. Exception Handling

* Exception records generated for:

  * Tax mismatches
  * Rule violations

Stored fields:

* Severity
* Rule name
* Message
* Timestamp

---

### 5. Reporting APIs

* Fetch all exceptions
* Filter by:

  * Customer ID
  * Rule name
  * Severity

Reports:

* Exception count by severity
* Customer tax summary

---

## 🔐 Security

* Spring Security implemented
* Authentication using DB user table
* Basic role-based setup

---

## ⚙️ Configuration

* SecurityConfig → Handles authentication & authorization
* DataInitializer → Preloads default data into database

## 🧪 Unit Testing (Requirement Covered)

### Covered Components:

* Service Layer
* Rule Engine

### Coverage Achieved:

* Service + Rule Engine: ~65%
* Overall Project: ~85%

✔ Requirement satisfied (Minimum 40–50%)

### Tools Used:

* JUnit 5
* Mockito
* EclEmma (Code Coverage in STS/Eclipse)

---

## 📂 Project Structure

com.avega.taxgap
├── controller
├── service
├── repository
├── entity
├── dto
├── enums
├── exception
├── config  
└── util



---

## ▶️ How to Run the Application

### 1. Clone Repository

git clone https://github.com/Ravi-Khot/tax-gap-service-final.git

git clone https://github.com/Ravi-Khot/tax-gap-service-final     //repo link without .git anywhere, use:


cd tax-gap-service

---

### 2. Configure Database

spring.application.name=tax-gap-service

server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/tax_gap_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=root@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.jackson.default-property-inclusion=NON_NULL

---

### 3. Run Application

Using STS:

* Right Click Project → Run As → Spring Boot App

OR using Maven:

mvn spring-boot:run

---

### 4. Access APIs

http://localhost:8080

---

## 📬 Sample Request (Transaction Upload)
POST  http://localhost:8080/api/transactions/upload

```json
[
  {
    "transactionId": "TXN1001",
    "date": "2026-04-13",
    "customerId": "CUST101",
    "amount": 1000,
    "taxRate": 0.18,
    "reportedTax": 180,
    "transactionType": "SALE"
  }
]
```

---

## 📊 Key Highlights

* Clean layered architecture
* Rule-based processing engine
* Strong validation handling
* Exception tracking system
* Scalable and modular design
* Good unit test coverage

---

## 📦 Deliverables

* Source Code (GitHub)
* Unit Tests
* README Documentation
* API Implementation
* Rule Engine Implementation

---

## 👨‍💻 Author

Ravi Khot
Full Stack Developer (Java + Spring Boot + Angular)
