# 🏠 RentFlow — Rental & Property Management System

RentFlow is a full-stack rental and property management system designed to help property owners manage properties, rental units, tenants, leases, rent cycles, payments, and receipts from a single platform.

The system provides separate experiences for **Owners** and **Tenants**, with JWT-based authentication and a complete rental payment workflow.

---

## 🚀 Project Overview

Managing rental properties manually can become difficult when dealing with multiple properties, tenants, leases, rent due dates, payments, and receipts.

RentFlow simplifies this workflow by providing a centralized system where:

- Owners can manage properties and rental units.
- Owners can manage tenants and leases.
- Rent cycles can be generated from active leases.
- Tenants can view their rent information.
- Tenants can make simulated digital payments.
- Owners can confirm pending payments.
- Partial payments are supported.
- Payment status is automatically reflected in rent cycles.
- Receipts can be generated and viewed as PDF documents.

---

## ✨ Key Features

### 🔐 Authentication

- JWT-based authentication
- Secure password handling using BCrypt
- Role-based application flow
- Separate Owner and Tenant dashboards
- Protected application routes

### 👨‍💼 Owner Module

Owners can manage the complete rental lifecycle.

#### Dashboard
- Overview of rental management information
- Centralized navigation for property management

#### Properties
- View rental properties
- Property information including:
  - Property name
  - Property type
  - Address
  - City
  - State
  - Country

#### Units
- View all rental units
- Add rental units
- View individual unit details
- Track:
  - Unit number
  - Monthly rent
  - Security deposit
  - Area
  - Occupancy status
  - Notes

#### Tenants
- View tenants
- Add new tenants
- View tenant details
- View tenant rental information

#### Leases
- Create and manage leases
- Associate tenants with rental units
- Configure:
  - Lease start date
  - Lease end date
  - Monthly rent
  - Security deposit
  - Payment due day
- Lease lifecycle management

#### Rent Cycles
- Generate rent cycles from leases
- Track:
  - Period start
  - Period end
  - Due date
  - Amount due
  - Amount paid
  - Balance due
  - Payment status

#### Payments
- View payment history
- View payment details
- Record payment information
- Confirm pending tenant payments
- Support payment methods such as UPI/cash
- Track transaction references
- Support partial payments

#### Receipts
- Generate receipts for confirmed payments
- Prevent duplicate receipts
- View receipt information
- Open generated receipt as a PDF

---

## 👨‍💻 Tenant Module

Tenants have a simplified dashboard focused on their rental obligations.

### Tenant Dashboard

Tenants can view:

- Active lease information
- Current rent cycle
- Monthly rent
- Amount paid
- Remaining balance
- Payment status
- Due date

### 💳 Payment Flow

The project currently uses a simulated payment workflow for development/demo purposes.

```text
Tenant
   ↓
View Rent Cycle
   ↓
Pay Rent
   ↓
Enter Payment Amount
   ↓
Simulated Payment
   ↓
Payment created as PENDING
   ↓
Owner confirms payment
   ↓
Rent Cycle updated
   ↓
Payment becomes CONFIRMED
