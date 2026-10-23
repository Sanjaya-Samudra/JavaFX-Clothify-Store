# 👗 Clothify Store — Enterprise POS & Fashion ERP System

> **Next-Generation Luxury Fashion Retail Point of Sale & Enterprise Resource Planning Platform**  
> Built with **JavaFX** (Modern Midnight Obsidian & Electric Indigo Theme) and a clean, decoupled **Layered / DAO Architecture** with **MySQL 8.4** backend persistence.

---

## ✨ Features Overview

### 1. 🛡️ Enterprise Security & Authentication
- **Salted SHA-256 Cryptographic Password Hashing**: Passwords stored as `salt$hash` with automatic migration for legacy credentials.
- **Role-Based Access Control (RBAC)**: Distinguishes between **Admin** (`ADMIN`) and **Cashier / Staff** (`CASHIER`).
- **Interactive Password Recovery**: "Forgot Password?" verification dialog allowing instant password reset.
- **Account Registration**: Self-service staff registration modal.
- **Executive Role Selection**: Direct toggle between **Administrator** and **Cashier** secure sign-in views.

### 2. 📊 Executive Dashboard & Live Analytics
- **Live KPI Counters**: Real-time Gross Sales today, Orders processed today, Active SKUs, and Low Stock alerts.
- **Interactive Sales Trends**: JavaFX Line Chart plotting daily revenue trajectory.
- **Critical Stock Radar**: Dynamic table highlighting inventory items with quantity $\le 5$ with color-coded warning chips.
- **Recent Orders Stream**: Live transaction log showing latest customer orders, payment methods, and net totals.
- **Live Header Clock**: Real-time ticking digital clock and date bar.

### 3. 🛍️ Point of Sale (POS) & Thermal Invoicing
- **Rapid Item Catalog**: Filter by category (*Gents*, *Ladies*, *Kids*, *Accessories*, *Footwear*, *Winter*) or search by barcode/name.
- **Interactive Shopping Cart**: Add, update quantity, or remove items with automatic stock bounds checking.
- **Live Price Engine**: Calculates Subtotal, Discount %, Net Total, Cash Tendered, and Balance / Change in real time.
- **Atomic Checkout Transactions**: Enforces complete transactional integrity across orders, line items, and stock deductions (`setAutoCommit(false)`, `commit()`, `rollback()`).
- **Thermal Receipt Generation**: Instant formatted invoice modal dialog with direct "Print / Save to File" capability.

### 4. 📦 Order Management & Restock Returns
- **Transaction History**: Searchable history by Order ID, Customer Name, or Payment Type.
- **Line-Item Inspection**: Modal popup displaying all purchased items for any selected order.
- **Product Return & Restock**: Process customer exchanges/returns, issue refunds, and automatically restore inventory stock levels.
- **Reprint Receipts**: One-click receipt reprinting for any historical transaction.

### 5. 👗 Product & Inventory Control
- **Full CRUD Management**: Register, update, search, and delete apparel items.
- **Variant Attributes**: Size (*XS, S, M, L, XL, XXL, FREE, 28-42*), Category, Unit Price, Stock Quantity, and Vendor.
- **Stock Health Badges**: Green ($>5$), Amber ($1-5$), Red ($0$ / Out of Stock).
- **Image Integration**: File chooser upload with image preview and binary BLOB persistence.
- **Automatic ID Generation**: Sequential human-friendly codes (`P001`, `P002`, `P011`...).

### 6. 🏢 Vendor & Supplier Directory
- **Vendor Profiles**: Supplier ID, Company Name, Contact Person, Email, Phone, and Supplied Category.
- **Full CRUD & Search**: Fast lookup by vendor name or company.

### 7. 👥 Staff & Workforce Management
- **Employee Directory**: Staff ID, Full Name, Contact Details, Branch Location, Role/Designation, and Salary.
- **Full CRUD**: Manage team members across store branches.

### 8. 📈 Business Intelligence & Audit Reports
- **Executive Valuation**: Live aggregation of total inventory asset valuation and stock units.
- **Monthly Revenue Chart**: Multi-month sales trajectory bar chart.
- **Top Selling Products**: Volume chart of top-performing fashion apparel.
- **Exportable Reports**:
  - *Sales Summary Report*
  - *Inventory & Valuation Audit Report*
  - *Vendor & Supplier Directory Report*
  - *Staff & Payroll Directory Report*
  - Direct file export to `reports/` folder.

---

## 🏗️ Architecture & Design Pattern

Clothify is designed strictly following **Layered Clean Architecture**:

```
clothify/src/main/java/
├── Main.java                        # Entry point launcher (classpath-safe)
├── Starter.java                     # JavaFX Application bootstrapper
├── config/                          # App constants & configurations
├── db/
│   ├── DBConnection.java            # Thread-safe Singleton JDBC Manager with atomic transactions
│   └── DBInitializer.java           # Automatic schema migration & seed data runner
├── security/
│   ├── PasswordSecurity.java        # Salted SHA-256 cryptographic hashing
│   └── UserSession.java             # Thread-safe active user & role context
├── model/                           # Domain Entities (User, Item, Order, OrderDetail, Supplier, Employee, CartItem)
├── dao/                             # Data Access Object contracts (CrudDao, UserDao, ItemDao, OrderDao, etc.)
│   └── impl/                        # JDBC implementations with parameterized PreparedStatement queries
├── service/ or controller/*/Service # Business logic layer orchestrating atomic transactions
├── controller/                      # JavaFX UI Controllers (POS, Overview, Items, Orders, Suppliers, Employees, Users, Reports)
└── util/                            # IdGenerator, AlertUtil, ValidationUtil, ReceiptGenerator
```

---

## 🚀 How to Run

### Method 1: Double-Click Launcher (Fastest)
Simply double-click:
```bat
run.bat
```
*(Located both in the root directory and inside the `clothify` folder)*

### Method 2: Maven Command
Inside the `clothify/` directory:
```bash
mvn clean compile
```

### Method 3: PowerShell Script
```powershell
.\clothify\run.ps1
```

---

## 🔑 Default Credentials

| Role | Email | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **👑 System Admin** | `admin@clothify.com` | `admin123` | Full enterprise administrator privileges (POS, Inventory, Staff, Suppliers, Reports, Users) |
| **💳 Cashier** | `cashier@clothify.com` | `cashier123` | Point of Sale, Catalog browse, Orders, Returns, Receipts |

*(Note: The system automatically migrates legacy passwords to salted SHA-256 hashes upon login)*