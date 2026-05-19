# Alfaleus CMS

A production-ready, SEO-optimized Content Management System built for deep-tech medical publishing.

## Features
- **Role-Based Access Control**: Secure login flow for Authors, Editors, and Admins.
- **Advanced Media Gallery**: Supports drag-and-drop local video (MP4/WebM) and image uploads, alongside YouTube embeds.
- **Aggressive Technical SEO**: 
  - Auto-generated `MedicalWebPage` JSON-LD Schema.
  - Dynamic `sitemap.xml` and `robots.txt`.
  - Open Graph preview generation.
  - Calculated reading time estimators.
- **Premium UI**: "Earthy Minimalist" design using Vanilla CSS and glassmorphism.

## Tech Stack
- **Backend:** Java 17, Spring Boot 3.x
- **Database:** MySQL 8.0, Spring Data JPA
- **Frontend:** Thymeleaf (SSR), Vanilla HTML/CSS
- **Editor:** Quill.js

## Local Setup
1. Clone the repository.
2. Ensure you have Java 17 and MySQL installed.
3. Start your local MySQL server.
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Navigate to `http://localhost:8080/admin/login` (Default credentials: `admin@alfaleus.com` / `password`).
