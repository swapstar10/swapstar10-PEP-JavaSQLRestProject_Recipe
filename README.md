# RecipeHub Backend 🍳

RecipeHub is a Java-based RESTful backend application designed to manage recipes, chefs, and ingredients. The application follows a layered architecture using Java, Javalin, JDBC, and SQL to provide efficient CRUD operations, authentication, and role-based access control.

The project focuses on clean separation of responsibilities between different layers, making the application scalable, maintainable, and easy to extend.

---

## 🚀 Features

### Recipe Management
- Create new recipes
- Retrieve all recipes
- Search recipes using keywords
- Update recipes
- Delete recipes
- Manage recipe-ingredient relationships

### Ingredient Management
- Add ingredients
- View ingredients
- Search ingredients
- Update ingredients
- Delete ingredients

### Chef Management
- Register chefs
- Login/logout functionality
- Retrieve chef information
- Update chef details
- Delete chef profiles
- Search chefs

### Authentication & Authorization
- Secure user authentication system
- Session-based login management
- Role-based access control (RBAC)
- Admin-only operations using middleware

Admin users can:
- Update ingredients
- Delete ingredients
- Delete recipes

Access restrictions are handled using:

```
AdminMiddleware
```

---

# 🏗️ Architecture

RecipeHub follows a layered backend architecture:

```
Client
  |
Controller Layer
  |
Service Layer
  |
DAO Layer
  |
Database Layer
```

## Model Layer

Contains Java entity classes representing application objects:

- Chef
- Recipe
- Ingredient

---

## DAO Layer

Responsible for database communication using JDBC.

Responsibilities:

- SQL query execution
- CRUD operations
- Database mapping
- Pagination support

Implemented DAOs:

```
ChefDAO
RecipeDAO
IngredientDAO
```

---

## Service Layer

Contains business logic between controllers and DAOs.

Responsibilities:

- Data validation
- Business rules
- Authentication handling
- Resource management

Services:

```
ChefService
RecipeService
IngredientService
AuthenticationService
```

---

## Controller Layer

Handles REST API requests and responses.

Controllers:

```
ChefController
RecipeController
IngredientController
AuthenticationController
```

Implemented using:

```
Javalin Framework
```

---

# 🛠️ Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Backend Programming Language |
| Javalin 5.6.1 | REST API Framework |
| JDBC | Database Connectivity |
| SQL | Database Management |
| Maven | Dependency Management |
| JUnit | Testing |
| Postman | API Testing |

---

# 🗄️ Database Design

Database schema is defined in:

```
sqlScript.sql
```

The database contains:

## Chef Table

Stores:

- Chef ID
- Name
- Email
- Password
- Role information

---

## Recipe Table

Stores:

- Recipe ID
- Recipe name
- Description
- Instructions
- Chef relationship

---

## Ingredient Table

Stores:

- Ingredient ID
- Ingredient name
- Quantity information

---

## RecipeIngredient Table

Handles many-to-many relationship:

```
Recipe <-----> Ingredient
```

This normalized design ensures efficient data storage and querying.

---

# 📂 Project Structure

```
RecipeHub Backend
│
├── src
│   ├── main
│   │   └── java
│   │       └── com.revature
│   │
│   │           ├── controller
│   │           │     ├── RecipeController.java
│   │           │     ├── ChefController.java
│   │           │     └── IngredientController.java
│   │
│   │           ├── dao
│   │           │     ├── RecipeDAO.java
│   │           │     ├── ChefDAO.java
│   │           │     └── IngredientDAO.java
│   │
│   │           ├── service
│   │           │     ├── RecipeService.java
│   │           │     ├── ChefService.java
│   │           │     └── AuthenticationService.java
│   │
│   │           ├── model
│   │           │     ├── Recipe.java
│   │           │     ├── Chef.java
│   │           │     └── Ingredient.java
│   │
│   │           └── util
│   │                 ├── ConnectionUtil.java
│   │                 ├── AdminMiddleware.java
│   │                 └── Pagination Utilities
│
├── postman
│   └── API Collection
│
├── pom.xml
├── README.md
└── sqlScript.sql
```

---

# 🔐 Authentication Flow

Authentication is handled through:

```
AuthenticationService
AuthenticationController
```

Workflow:

```
User Registration
        |
        ↓
Database Storage
        |
        ↓
Login Validation
        |
        ↓
Session Creation
        |
        ↓
Authorized Requests
```

---

# 📑 Pagination Support

Large datasets are handled using pagination utilities:

```
Page
PageOption
```

Benefits:

- Improved performance
- Reduced database load
- Efficient data retrieval

---

# 🌐 API Endpoints

## Authentication

| Method | Endpoint | Description |
|-|-|-|
| POST | /register | Register chef |
| POST | /login | User login |
| POST | /logout | Logout user |

---

## Recipes

| Method | Endpoint | Description |
|-|-|-|
| GET | /recipes | Get recipes |
| POST | /recipes | Create recipe |
| PUT | /recipes/{id} | Update recipe |
| DELETE | /recipes/{id} | Delete recipe |

---

## Ingredients

| Method | Endpoint | Description |
|-|-|-|
| GET | /ingredients | Get ingredients |
| POST | /ingredients | Add ingredient |
| PUT | /ingredients/{id} | Update ingredient |
| DELETE | /ingredients/{id} | Delete ingredient |

---

# ▶️ Running the Application

## Prerequisites

Install:

- Java 21+
- Maven
- SQL Database


Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

---

## Database Setup

1. Create the database.

2. Execute:

```
sqlScript.sql
```

3. Configure database credentials in:

```
ConnectionUtil.java
```

---

## Build Project

Run:

```bash
mvn clean install
```

---

## Run Application

Start the Javalin server:

```bash
mvn exec:java
```

Application starts on:

```
http://localhost:8082
```

---

# 🧪 Testing

Tests are implemented using JUnit.

Run:

```bash
mvn test
```

API testing can be performed using the included Postman collection:

```
postman/
```

---
# 👨‍💻 Author

**Swapnil Lakhera**

Java Backend Developer | SQL | REST APIs | JDBC | Javalin

---

# 📌 Future Improvements

- JWT-based authentication
- Swagger API documentation
- Docker containerization
- Cloud deployment
- Frontend integration

