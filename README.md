# 💰 Finance Dashboard Backend

A robust Spring Boot REST API backend for managing personal/business finances with role-based access control, comprehensive analytics, and real-time dashboard insights.

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Setup Instructions](#-setup-instructions)
- [API Endpoints](#-api-endpoints)
- [Authentication & RBAC](#-authentication--rbac)
- [Database Schema](#-database-schema)
- [Code Structure](#-code-structure)
- [Sample API Requests](#-sample-api-requests)
- [Assumptions](#-assumptions)
- [Trade-offs](#-trade-offs)
- [Future Improvements](#-future-improvements)

---

## 🎯 Project Overview

Finance Dashboard is a backend application that provides APIs for managing financial records, users, and generating analytical insights. It enables organizations to track income and expenses, manage user access based on roles, and visualize financial trends through aggregated dashboard data.

### Key Features

- **User Management** – Create, update, and manage users with different access levels
- **Financial Records** – Full CRUD operations for income/expense tracking
- **Role-Based Access Control (RBAC)** – Secure endpoints based on user roles
- **Dashboard Analytics** – Real-time summaries, category breakdowns, and monthly trends
- **Advanced Search & Filtering** – Filter records by type, category, date range, and keywords
- **Pagination & Sorting** – Efficient data retrieval with customizable page sizes
- **Soft Delete** – Recoverable deletion of financial records

---

## 🛠 Tech Stack

| Technology            | Purpose                              |
| --------------------- | ------------------------------------ |
| **Java 21**           | Programming Language                 |
| **Spring Boot 3.2.4** | Application Framework                |
| **Spring Data JPA**   | Database ORM                         |
| **MySQL 8.x**         | Relational Database                  |
| **Hibernate 6**       | JPA Implementation                   |
| **Lombok**            | Boilerplate Reduction                |
| **JWT (jjwt 0.12.5)** | Token-based Authentication           |
| **Spring AOP**        | Aspect-Oriented Programming for RBAC |
| **Maven**             | Build & Dependency Management        |

---

## ✨ Features

### Core Functionality

| Feature                      | Description                            |
| ---------------------------- | -------------------------------------- |
| ✅ User CRUD                 | Create, read, update, delete users     |
| ✅ Financial Records CRUD    | Manage income and expense entries      |
| ✅ JWT Authentication        | Secure token-based authentication      |
| ✅ Role-Based Access         | ADMIN, ANALYST, VIEWER permissions     |
| ✅ Dashboard Summary         | Total income, expense, net balance     |
| ✅ Category Analytics        | Spending breakdown by category         |
| ✅ Monthly Trends            | Income/expense trends over time        |
| ✅ Recent Transactions       | Quick view of latest activity          |
| ✅ Advanced Filtering        | Filter by type, category, date, amount |
| ✅ Keyword Search            | Search in notes and categories         |
| ✅ Pagination                | Configurable page size (1-100)         |
| ✅ Sorting                   | Sort by any field, asc/desc            |
| ✅ Soft Delete               | Recoverable record deletion            |
| ✅ Input Validation          | Request body validation                |
| ✅ Global Exception Handling | Consistent error responses             |
| ✅ Comprehensive Logging     | SLF4J logging throughout               |

---

## 🚀 Setup Instructions

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **MySQL 8.x**

### Installation Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/finance-dashboard.git
   cd finance-dashboard
   ```

2. **Create MySQL database**

   ```sql
   CREATE DATABASE finance_dashboard;
   ```

3. **Configure database connection**

   Edit `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_dashboard
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build the project**

   ```bash
   mvn clean install
   ```

5. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

6. **Access the API**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

### Initial Setup

Insert an admin user to get started:

```sql
INSERT INTO users (name, email, role, status)
VALUES ('Admin User', 'admin@example.com', 'ADMIN', 'ACTIVE');
```

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint      | Description             | Access |
| ------ | ------------- | ----------------------- | ------ |
| POST   | `/auth/login` | Login and get JWT token | Public |

### User Management

| Method | Endpoint      | Description     | Access    |
| ------ | ------------- | --------------- | --------- |
| GET    | `/users`      | Get all users   | All Roles |
| GET    | `/users/{id}` | Get user by ID  | All Roles |
| POST   | `/users`      | Create new user | ADMIN     |
| PUT    | `/users/{id}` | Update user     | ADMIN     |
| DELETE | `/users/{id}` | Delete user     | ADMIN     |

### Financial Records

| Method | Endpoint                | Description                | Access    |
| ------ | ----------------------- | -------------------------- | --------- |
| GET    | `/records`              | Get records (with filters) | All Roles |
| GET    | `/records/{id}`         | Get record by ID           | All Roles |
| POST   | `/records`              | Create new record          | ADMIN     |
| PUT    | `/records/{id}`         | Update record              | ADMIN     |
| DELETE | `/records/{id}`         | Soft delete record         | ADMIN     |
| GET    | `/records/deleted`      | Get deleted records        | ADMIN     |
| POST   | `/records/{id}/restore` | Restore deleted record     | ADMIN     |

#### Query Parameters for GET /records

| Parameter   | Type    | Description                        | Example                 |
| ----------- | ------- | ---------------------------------- | ----------------------- |
| `type`      | String  | Filter by INCOME or EXPENSE        | `?type=EXPENSE`         |
| `category`  | String  | Filter by category (partial match) | `?category=food`        |
| `keyword`   | String  | Search in notes and category       | `?keyword=rent`         |
| `startDate` | Date    | Records on or after date           | `?startDate=2024-01-01` |
| `endDate`   | Date    | Records on or before date          | `?endDate=2024-12-31`   |
| `minAmount` | Double  | Minimum amount filter              | `?minAmount=100`        |
| `maxAmount` | Double  | Maximum amount filter              | `?maxAmount=5000`       |
| `page`      | Integer | Page number (0-based)              | `?page=0`               |
| `size`      | Integer | Page size (1-100)                  | `?size=20`              |
| `sortBy`    | String  | Sort field                         | `?sortBy=amount`        |
| `order`     | String  | Sort direction (asc/desc)          | `?order=desc`           |

### Dashboard Analytics

| Method | Endpoint                      | Description                        | Access    |
| ------ | ----------------------------- | ---------------------------------- | --------- |
| GET    | `/dashboard/summary`          | Total income, expense, net balance | All Roles |
| GET    | `/dashboard/category-summary` | Totals grouped by category         | All Roles |
| GET    | `/dashboard/recent`           | Last 10 transactions               | All Roles |
| GET    | `/dashboard/monthly-trends`   | Monthly income/expense breakdown   | All Roles |

---

## 🔐 Authentication & RBAC

### Authentication Flow

1. **Login**: Send email to `/auth/login` to receive JWT token
2. **Use Token**: Include token in all subsequent requests
   ```
   Authorization: Bearer <your_jwt_token>
   ```

### Role Definitions

| Role        | Description                                       |
| ----------- | ------------------------------------------------- |
| **ADMIN**   | Full system access – manage users and all records |
| **ANALYST** | Read access to all data including analytics       |
| **VIEWER**  | Read-only access to records and dashboard         |

### Access Control Matrix

| Resource                   | ADMIN | ANALYST | VIEWER |
| -------------------------- | ----- | ------- | ------ |
| View Users                 | ✅    | ✅      | ✅     |
| Create/Edit/Delete Users   | ✅    | ❌      | ❌     |
| View Records               | ✅    | ✅      | ✅     |
| Create/Edit/Delete Records | ✅    | ❌      | ❌     |
| View Deleted Records       | ✅    | ❌      | ❌     |
| Restore Records            | ✅    | ❌      | ❌     |
| Dashboard Analytics        | ✅    | ✅      | ✅     |

### Implementation

RBAC is implemented using:

- **Custom Annotations**: `@AdminOnly`, `@ReadAccess`, `@AnalystAccess`
- **Spring AOP**: Aspect intercepts annotated methods
- **JWT Filter**: Validates token and extracts user context

---

## 🗄 Database Schema

### Entity Relationship Diagram

```
┌──────────────────┐       ┌──────────────────────┐
│      users       │       │   financial_records  │
├──────────────────┤       ├──────────────────────┤
│ id (PK)          │──────<│ id (PK)              │
│ name             │       │ amount               │
│ email (UNIQUE)   │       │ type (ENUM)          │
│ role (ENUM)      │       │ category             │
│ status (ENUM)    │       │ date                 │
└──────────────────┘       │ notes                │
                           │ user_id (FK)         │
                           │ is_deleted           │
                           │ deleted_at           │
                           └──────────────────────┘
```

### Users Table

| Column | Type         | Description            |
| ------ | ------------ | ---------------------- |
| id     | BIGINT       | Primary key            |
| name   | VARCHAR(255) | User's full name       |
| email  | VARCHAR(255) | Unique email address   |
| role   | ENUM         | ADMIN, ANALYST, VIEWER |
| status | ENUM         | ACTIVE, INACTIVE       |

### Financial Records Table

| Column     | Type         | Description          |
| ---------- | ------------ | -------------------- |
| id         | BIGINT       | Primary key          |
| amount     | DOUBLE       | Transaction amount   |
| type       | ENUM         | INCOME, EXPENSE      |
| category   | VARCHAR(255) | Category name        |
| date       | DATE         | Transaction date     |
| notes      | VARCHAR(255) | Optional description |
| user_id    | BIGINT       | Foreign key to users |
| is_deleted | BOOLEAN      | Soft delete flag     |
| deleted_at | DATETIME     | Deletion timestamp   |

---


---

## 📝 Sample API Requests

### Create Financial Record

**Request:**

```http
POST /records
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-04-01",
    "notes": "Monthly salary for April",
    "userId": 1
}
```

**Response (201 Created):**

```json
{
  "success": true,
  "message": "Financial record created successfully",
  "data": {
    "id": 1,
    "amount": 5000.0,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-04-01",
    "notes": "Monthly salary for April",
    "userId": 1,
    "userName": "Admin User"
  },
  "timestamp": "2024-04-02T10:30:00"
}
```

### Get Dashboard Summary

**Request:**

```http
GET /dashboard/summary
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Dashboard summary fetched successfully",
  "data": {
    "totalIncome": 15500.0,
    "totalExpense": 4230.0,
    "netBalance": 11270.0
  },
  "timestamp": "2024-04-02T10:35:00"
}
```

---

## 📌 Assumptions

1. **Single Currency** – All amounts are in one currency (no conversion)
2. **Single User Ownership** – Each financial record belongs to one user
3. **No Password Authentication** – JWT is generated based on email lookup (simplified for demo)
4. **Soft Delete Only** – Financial records are soft-deleted, not permanently removed
5. **Server-Side Filtering** – All filtering happens at database level for efficiency
6. **Active Users Only** – Inactive users cannot authenticate

---

## ⚖️ Trade-offs

| Decision                             | Trade-off                                                   |
| ------------------------------------ | ----------------------------------------------------------- |
| **JWT without password**             | Simplified auth for demo; production needs password/OAuth   |
| **No caching**                       | Dashboard queries hit DB each time; could add Redis         |
| **Synchronous processing**           | All operations are blocking; async could improve throughput |
| **Single database**                  | No read replicas; works for moderate load                   |
| **JPA Specifications**               | Dynamic queries vs raw SQL; slightly less performant        |
| **Soft delete with @SQLRestriction** | Automatic filtering but bypassed in native queries          |

---

## 🚧 Future Improvements

### Security Enhancements

- [ ] Password-based authentication with BCrypt
- [ ] OAuth 2.0 / Social login integration
- [ ] Refresh token mechanism
- [ ] Rate limiting per user/IP

### Performance

- [ ] Redis caching for dashboard aggregations
- [ ] Database connection pooling optimization
- [ ] Async processing for bulk operations
- [ ] Database indexing for frequently filtered columns

### Features

- [ ] Multi-currency support with conversion
- [ ] Budget planning and alerts
- [ ] Recurring transactions
- [ ] File attachments (receipts)
- [ ] Export to CSV/PDF

### DevOps

- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] API documentation with Swagger/OpenAPI
- [ ] Health checks and monitoring

---


## 👤 Author

Built as a backend assessment project demonstrating Spring Boot REST API development 

---
