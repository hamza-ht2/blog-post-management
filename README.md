# Blog Management System

A RESTful API built with Spring Boot for managing blog posts, comments, likes, and user interactions.

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **Spring Validation**
- **Thymeleaf**
- **MySQL**
- **Maven**

## Features

- User registration with secure password encryption
- Full post lifecycle management — create, edit, publish, unpublish, and delete
- Admin controls — reject inappropriate posts, enable/disable accounts, manage roles
- Social interactions — like/unlike posts, comment, follow/unfollow authors
- Content organization through categories and tags
- Author profiles with bio, country, website, and profile photo
- Auto-generated slugs and reading time for every post
- Search posts by title and filter by category, tag, author, or status
- Follower and following system per user

## Project Structure
```
src/
├── models/
│   ├── enums/
├── repositories/
├── services/
└── controllers/ (coming soon)
```

## Getting Started

1. Clone the repo
2. Create a MySQL database called `blog_db`
3. Update `application.properties` with your MySQL credentials
4. Run the project
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blog_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Status

🚧 Under active development
