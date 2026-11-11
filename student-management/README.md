# Student Registration and Management System

A web application for registering students and listing them, built with **Spring MVC, Thymeleaf, Java and PostgreSQL**. All database access uses plain **JDBC** with hand-written SQL (no JPA / Hibernate).

## Technologies Used
- Java 17, Spring Boot 3.3 (Spring MVC, Bean Validation)
- Thymeleaf (server-side HTML templates)
- PostgreSQL + JDBC (`Connection`, `PreparedStatement`, `ResultSet`)
- Maven

## Project Structure
```
src/main/java/com/example/studentmanagement
├── StudentManagementApplication.java
├── config/       DatabaseConfig.java        (DataSource bean)
├── controller/   StudentController.java
├── service/      StudentService.java
├── repository/   StudentRepository.java     (JDBC)
├── model/        Student.java
└── exception/    StudentException.java
src/main/resources
├── application.properties                    (reads env variables)
├── templates/    register.html, students.html
└── static/css/   style.css
docs/schema.sql
```

## Database Setup
```sql
CREATE DATABASE student_management;
\c student_management

CREATE TABLE students (
    id        SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    age       INTEGER NOT NULL CHECK (age > 18),
    email     VARCHAR(150) NOT NULL UNIQUE
);
```
The form collects `firstName` and `lastName` separately; the repository joins them into `full_name` before the INSERT, and splits them again (at the first space) when reading.

## Running the Application
Set the database credentials as environment variables (nothing secret is stored in the repo):

```bash
# Linux / macOS
export DB_URL=jdbc:postgresql://localhost:5432/student_management
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# Windows PowerShell
$env:DB_PASSWORD="your_password"

mvn spring-boot:run
```
Open http://localhost:8080/student/register

## IoC, Bean Creation and Dependency Injection
**Inversion of Control (IoC):** instead of our code creating and wiring objects with `new`, the Spring IoC container does it. On startup, `@SpringBootApplication` triggers component scanning of `com.example.studentmanagement`.

**Bean creation:** the container finds classes annotated with stereotype annotations and registers them as beans:
- `@Repository` → `StudentRepository`
- `@Service` → `StudentService`
- `@Controller` → `StudentController`
- `@Configuration` + `@Bean` → `DataSource` (in `DatabaseConfig`)

**Dependency Injection (constructor-based):** Spring reads each constructor and injects the required beans:

```
DataSource  ──►  StudentRepository  ──►  StudentService  ──►  StudentController
```
1. `DatabaseConfig` creates the `DataSource` bean from environment-based properties.
2. `StudentRepository(DataSource)` receives the DataSource.
3. `StudentService(StudentRepository)` receives the repository.
4. `StudentController(StudentService)` receives the service.

No class creates its own dependencies with `new`, so layers stay loosely coupled and easy to test/replace.

## Controller Handler Mappings
`StudentController` is annotated with `@Controller` and `@RequestMapping("/student")` (common base path).

| Method | Endpoint | Handler | View |
|--------|----------|---------|------|
| GET  | `/student/register` | `showRegistrationForm` (`@GetMapping`) | `register.html` |
| POST | `/student/register` | `registerStudent` (`@PostMapping`, `@ModelAttribute`) | redirects to `/student/register` with success message |
| GET  | `/student/all`      | `listStudents` (`@GetMapping`) | `students.html` |
| GET  | `/student/{id}`     | `viewStudent` (`@GetMapping`, `@PathVariable`) — bonus | `students.html` (reused) |

`Model` carries data (the `Student` object, the student list, messages) to the Thymeleaf views. `@Valid` validates the form (all fields required, age > 0, valid email). Database problems (duplicate email, age ≤ 18) are caught and shown as friendly messages.

## Screenshots
_Add your screenshots here:_
- Registration page: `docs/register.png`
- Student list: `docs/students.png`
- Saved records in PostgreSQL: `docs/database.png`
