RecipeHub (Back-End)
Overview

RecipeHub is a Java-based backend system built to manage recipes, chefs, and ingredients through a layered architecture that emphasizes clean separation of concerns. The platform leverages object-oriented programming principles and a relational database to deliver secure, efficient data management. Certain actions, such as updating or deleting resources, are restricted to admin users, ensuring proper role-based access control.

Architecture & Components

The project follows a standard layered structure:

Model Layer: Represents core domain objects like Chef, Recipe, and Ingredient.

DAO Layer: Handles data access and integrates pagination via utility classes (Page and PageOption) to manage large datasets efficiently.

Service Layer: Encapsulates business logic and mediates between controllers and DAOs.

Controller Layer: Defines endpoints for user interaction with recipes, chefs, and ingredients.

Utilities: Includes database connection management (ConnectionUtil), pagination helpers, and AdminMiddleware for access control.

Database Design

The database schema is defined in sqlScript.sql. It provides structured tables for:

Chef – Stores user and role information.

Recipe – Represents culinary entries created by chefs.

Ingredient – Captures all possible components used in recipes.

RecipeIngredient – A join table managing the many-to-many relationship between recipes and ingredients.

This schema ensures normalized data storage and efficient querying.

Core Features

Database Initialization
RecipeHub sets up all necessary tables for chefs, recipes, and ingredients, ensuring the backend is fully equipped for data persistence and retrieval.

Recipe Management
Users can create, update, delete, and search recipes by keyword. This functionality is implemented through RecipeDAO, RecipeService, and RecipeController.

Ingredient Management
Ingredients can be added, updated, deleted, or searched, ensuring flexible control over recipe composition. These features are handled within the IngredientDAO, IngredientService, and IngredientController.

Chef Management
Chef information can be maintained with full CRUD operations and keyword search. This ensures recipes are properly attributed to their creators and chef profiles remain up-to-date (ChefDAO and ChefService).

Authentication & Access Control

Chefs can register, log in, and log out, providing a secure entry point for the system.

AuthenticationService and AuthenticationController govern this functionality.

Admin-only restrictions are enforced by AdminMiddleware, ensuring only privileged users can update or delete ingredients and delete recipes.

Technology Stack

Language: Java (compatible with versions 9–22)

Database: Relational SQL (schema defined in sqlScript.sql)

Frameworks & Libraries: Javalin (for routing and controllers), JDBC (for DB operations), and supporting utilities.

Architecture: Layered (Model–DAO–Service–Controller)


