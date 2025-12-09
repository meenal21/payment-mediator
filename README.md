# Payment Mediator  
Event-Driven Payment Workflow — Spring Boot + Mediator Pattern + Outbox Pattern

---

## Overview

This backend service implements a **complete payment lifecycle engine** for marketplace-style platforms.  
It follows modern event-driven design principles using:

- **Mediator Pattern** for orchestrating state transitions  
- **Outbox Pattern** for reliable command/event processing  
- **Command Handlers** for modular workflow steps  
- **Domain Events** for state change communication  
- **State Machine** representing end-to-end payment flow  

This service can later integrate with **Apache Kafka** to support microservices and distributed event processing.

---

# Architecture Overview

The payment flow is implemented as a **state machine** driven by **commands** and **events**, coordinated by the **PaymentMediator** and executed reliably using the **OutboxProcessor**.

### High-Level Flow
```
CREATED
↓
TAXED
↓ (User clicks Pay)
AUTHORIZED
↓
ESCROW_CREATED
↓ (User clicks Delivered)
DELIVERED
↓
CAPTURED
↓
PAYOUT_COMPLETED
```
Every transition is triggered by a **Command → Event → Mediator Decision**.




# 🗄 Database Tables

## **1. payment**

| Column       | Description                       |
|--------------|-----------------------------------|
| id           | Payment ID                        |
| buyer_id     | Buyer ID                          |
| seller_id    | Seller ID                         |
| amount       | Base order amount                 |
| tax_amount   | Calculated tax amount             |
| state        | Payment state enum                |
| created_at   | Timestamp                         |
| updated_at   | Timestamp                         |

---

## **2. outbox_messages**

| Column         | Description                                 |
|----------------|---------------------------------------------|
| id             | Outbox entry ID                             |
| aggregate_id   | Payment ID                                  |
| message_type   | COMMAND / EVENT                             |
| message_name   | e.g. `CALCULATE_TAX`, `AUTHORIZE_PAYMENT`   |
| payload        | JSON payload                                |
| status         | PENDING / PROCESSED / FAILED                |
| created_at     | Timestamp                                   |
| processed_at   | Timestamp                                   |

---

## **3. idempotency_keys**

Ensures checkout cannot be created twice.

| Column           | Description               |
|------------------|---------------------------|
| idempotency_key  | Unique client key (PK)    |
| created_at       | Timestamp                 |

---

# 🌐 API Endpoints

### **1️⃣ Create Checkout**
```bash
POST /payments/checkout
Headers:
  Idempotency-Key: <uuid>

Body:
{
  "buyerId": 1,
  "sellerId": 20,
  "amount": 1500
}
```

### **2️⃣ Simulate Payment (User Clicks Pay)**
```bash
POST /payments/{id}/pay
```

Transitions:
TAXED → AUTHORIZED → ESCROW_CREATED


### **3️⃣ Confirm Delivery**
```bash
GET /payments/{id}/status
```

## Running the Backend

```bash
mvn spring-boot:run
```

## Technologies Used
- Java 17
- Spring Boot
- Spring Data JPA
- H2 / MySQL
- Lombok
- Mediator Pattern
- Outbox Pattern
- Event-Driven Architecture

## Future: Kafka Integration

This project is intentionally designed to plug into Kafka easily:
OutboxProcessor → publish command/event messages to Kafka
Mediator → consume events from Kafka
External microservices can handle tax, authorization, payout
Building this into a distributed payment pipeline requires almost zero refactoring.

