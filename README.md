# BankIT Loan Management System

A robust Java-based desktop application for managing loan operations, built with JavaFX and SQLite. This system provides comprehensive loan management capabilities including loan creation, modification, document generation, and data export/import features.

![BankIT Main Interface](screenshots/create_new_loan.png)

## Contents
- [Features](#features)
  - [Loan Management](#1-loan-management)
  - [Officer Management](#2-officer-management)
  - [Document Generation](#3-document-generation)
  - [Data Management](#4-data-management)
  - [User Interface](#5-user-interface)
- [Technology Stack](#technology-stack)
- [Architecture & Design Patterns](#architecture--design-patterns)
  - [Architecture](#architecture)
  - [Project Structure](#project-structure)
- [Design Patterns](#design-patterns)
  - [Singleton Pattern](#singleton-pattern)
  - [Factory Pattern](#factory-pattern)
  - [Builder Pattern](#builder-pattern)
  - [Observer Pattern](#observer-pattern)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Setup Steps](#setup-steps)
- [Usage Guide](#usage-guide)

## Features

### 1. Loan Management
- Create new loan applications with detailed customer information
- Edit existing loan records
- Auto-generate unique loan IDs
- Real-time loan calculation with monthly and total payment details
- Support for multiple account types (Savings, Checking, Joint)

### 2. Officer Management
- Assign loan officers to applications
- Create new officer profiles
- Automatic officer information retrieval

### 3. Document Generation
- Generate professional PDF loan agreements
![Loan Agreement](screenshots/loan_agreement.png)
- Export internal bank documents
![Internal Document](screenshots/bank_document.png)
- Standardized formatting and layout for all documents

### 4. Data Management
- SQLite database integration for persistent storage
- Export loan data to CSV format
![CSV Export](screenshots/csv_export.png)
- Import loan data from CSV files
- Data validation and error handling

### 5. User Interface
- Modern, intuitive JavaFX interface
- Real-time form validation
- Responsive table view with search functionality
- Dark mode support

## Technology Stack

- Java 18
- JavaFX 21.0.2
- SQLite 3.44.1
- PDFBox 2.0.27
- Maven
- CSS3
- Apache PDFBox

## Architecture & Design Patterns

### Architecture
- Model-View-Controller (MVC) pattern
- Data Access Object (DAO) pattern
- Service Layer pattern
- Factory pattern

```layered
Presentation Layer (JavaFX Views)
↓
Controller Layer (JavaFX Controllers)
↓
Service Layer (Business Logic)
↓
DAO Layer (Data Access)
↓
Database (SQLite)
```

### Project Structure
The project follows a modular architecture with clear separation of concerns:
```layered
src/
├── main/
│   ├── java/
│   │   └── com/bankit/loan/
│   │       ├── config/       # Configuration classes
│   │       ├── controller/   # JavaFX controllers
│   │       ├── dao/          # Data access layer
│   │       ├── model/        # Domain models
│   │       ├── service/      # Business logic
│   │       └── util/         # Utility classes
│   └── resources/
│       ├── css/             # Stylesheets
│       ├── db/              # Database files
│       ├── images/          # Application assets
│       └── view/fxml/       # JavaFX layouts
```

## Design Patterns

### Singleton Pattern
- ServiceFactory
- DAOFactory
- DatabaseConfig

### Factory Pattern
- Service creation
- DAO creation

### Builder Pattern
- Document generation
- PDF creation

### Observer Pattern
- UI updates
- Form validation

## Installation

### Prerequisites
- Java JDK 17 or higher
- Maven 3.8.x or higher
- Git

### Setup Steps
1. Clone the repository and switch to JavaFX branch
```bash
git clone https://github.com/CharlieBrown018/Loan-Management-System.git
cd Loan-Management-System
git checkout javaFX
```
2. Build the project
```bash
mvn clean install
```
3. Run the application
```bash
mvn javafx:run
```

## Usage Guide
1. Creating a New Loan:
- Fill in Customer Information fields
- Generate a Loan ID
- Complete Loan Information
- Select or create Officer Information
- Click Calculate then Save
2. Editing Existing Loans:
- Select loan record from table
- Modify desired fields
- Click Update to save changes
3. Document Generation:
- Select loan record
- Click "Print Loan Agreement" for customer copy
- Click "Export Bank Document" for internal copy
4. Data Export/Import:
- Use Export button for CSV export
- Use Import button to load CSV data
- Use Refresh to update table view
