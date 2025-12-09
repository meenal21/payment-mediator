# 🧾 Payment Mediator Backend  
Event-Driven Payment Workflow — Spring Boot + Mediator Pattern + Outbox Pattern

---

## 🚀 Overview

This backend service implements a **complete payment lifecycle engine** for marketplace-style platforms.  
It follows modern event-driven design principles using:

- **Mediator Pattern** for orchestrating state transitions  
- **Outbox Pattern** for reliable command/event processing  
- **Command Handlers** for modular workflow steps  
- **Domain Events** for state change communication  
- **State Machine** representing end-to-end payment flow  

This service can later integrate with **Apache Kafka** to support microservices and distributed event processing.

---

# 🏗 Architecture Overview

The payment flow is implemented as a **state machine** driven by **commands** and **events**, coordinated by the **PaymentMediator** and executed reliably using the **OutboxProcessor**.

### 🔵 High-Level Flow

