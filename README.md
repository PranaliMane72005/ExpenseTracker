# 💰 FinTrack — Personal Finance Tracker

A full-stack **Java Spring Boot** application for tracking income, expenses, budgets, savings goals, and financial trends — with a polished dark-mode SPA frontend.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21 + Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.12) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (dev) / PostgreSQL (prod) |
| PDF Export | iTextPDF 5 |
| CSV Export | Apache Commons CSV |
| Frontend | Vanilla JS + Chart.js 4 |
| Build | Maven |

---

## 📋 Modules (per Project Spec)

| Milestone | Module | Weeks | Status |
|---|---|---|---|
| 1 | User Auth & Profile (JWT, roles) | 1–2 | ✅ |
| 2 | Expense & Income Tracking | 3–4 | ✅ |
| 3 | Budget & Savings Goals | 5–6 | ✅ |
| 4 | Financial Trends & Visualization | 7 | ✅ |
| 5 | Export (PDF/CSV) & Community Forum | 8 | ✅ |

---

## ⚡ Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+

### Run

```bash
cd finance-tracker
mvn spring-boot:run
```

Open **http://localhost:8080**

### Demo Credentials

| Role | Email | Password |
|---|---|---|
| User | demo@finance.com | demo123 |
| Admin | admin@finance.com | admin123 |

---

## 🗂 Project Structure

```
src/main/java/com/financetracker/
├── FinanceTrackerApplication.java      # Entry point
├── model/
│   ├── User.java                       # Users table (id, name, email, role…)
│   ├── Transaction.java                # Transactions (income/expense + category)
│   ├── Budget.java                     # Monthly budgets per category
│   ├── SavingsGoal.java               # Savings targets + progress
│   ├── ForumPost.java                  # Community posts
│   ├── ForumComment.java              # Post comments
│   └── Export.java                    # Export audit log
├── repository/                         # Spring Data JPA repositories
├── service/
│   ├── AuthService.java               # Registration, login, profile
│   ├── TransactionService.java        # CRUD + summaries + trends
│   ├── BudgetService.java             # Budget tracking with spend calc
│   ├── SavingsGoalService.java        # Goal progress management
│   ├── ForumService.java              # Posts, comments, likes
│   └── ExportService.java             # PDF + CSV generation
├── controller/                         # REST API endpoints
├── security/
│   ├── JwtUtils.java                  # Token generation & validation
│   ├── JwtAuthenticationFilter.java   # Request filter
│   └── UserDetailsServiceImpl.java    # User loading
├── config/
│   ├── SecurityConfig.java            # CORS, JWT, route protection
│   ├── WebMvcConfig.java              # Static file + SPA routing
│   ├── DataInitializer.java           # Demo seed data
│   └── GlobalExceptionHandler.java    # Unified error responses
└── dto/                               # Request/Response DTOs

src/main/resources/
├── application.properties             # Config (H2, JWT, etc.)
└── static/
    ├── index.html                     # SPA shell
    └── js/app.js                      # Full frontend logic
```

---

## 🔌 REST API Reference

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login, returns JWT |
| GET | /api/auth/me | Get current user profile |
| PUT | /api/auth/profile | Update profile |

### Transactions
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/transactions | List all (optional ?startDate=&endDate=) |
| POST | /api/transactions | Create transaction |
| PUT | /api/transactions/{id} | Update transaction |
| DELETE | /api/transactions/{id} | Delete transaction |
| GET | /api/transactions/summary | Monthly summary (income/expenses/categories) |
| GET | /api/transactions/trends | Monthly trend data (?type=INCOME\|EXPENSE) |

### Budgets
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/budgets | All budgets (?currentOnly=true) |
| POST | /api/budgets | Create budget |
| PUT | /api/budgets/{id} | Update budget |
| DELETE | /api/budgets/{id} | Delete budget |

### Savings Goals
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/savings-goals | All goals |
| POST | /api/savings-goals | Create goal |
| PUT | /api/savings-goals/{id} | Update goal |
| PATCH | /api/savings-goals/{id}/add-amount | Add saved amount |
| DELETE | /api/savings-goals/{id} | Delete goal |

### Export
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/export/pdf | Download PDF report |
| GET | /api/export/csv | Download CSV file |

### Community Forum
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/forum/posts | List all posts |
| POST | /api/forum/posts | Create post |
| POST | /api/forum/posts/{id}/like | Like a post |
| GET | /api/forum/posts/{id}/comments | Get comments |
| POST | /api/forum/posts/{id}/comments | Add comment |
| POST | /api/forum/comments/{id}/like | Like a comment |

---

## 🗄 Database Schema

```
users           → id, name, email, password, role, monthly_income, …
transactions    → id, user_id, type, amount, category, description, date, …
budgets         → id, user_id, category, amount, start_date, end_date
savings_goals   → id, user_id, goal_name, target_amount, current_amount, deadline, status
forum_posts     → id, user_id, title, content, likes, created_at
forum_comments  → id, post_id, user_id, comment, likes, created_at
exports         → id, user_id, format, exported_at
```

---

## 🌍 Switch to PostgreSQL

Update `application.properties`:

```properties
# Comment out H2 block, uncomment these:
spring.datasource.url=jdbc:postgresql://localhost:5432/financedb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## 📊 Frontend Features

- **Dashboard** — Monthly income/expense stats, doughnut chart by category, bar chart (6-month income vs expenses), recent transactions
- **Transactions** — Full CRUD with type/category/text filters
- **Budget** — Category budgets with animated progress bars, overspend alerts
- **Savings Goals** — Visual goal cards with progress, add-amount flow
- **Trends** — Line charts for expense/income history, category bar chart
- **Export** — One-click PDF/CSV download
- **Community Forum** — Posts, threaded comments, likes
- **Profile** — Edit name, income targets, currency preference
