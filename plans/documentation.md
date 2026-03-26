# Overview of Application

The Course Recovery System is a web-based Jakarta EE application that manages student academic performance and course recovery programs. It follows a four-tier architecture with a clear split across Presentation, Business, Data Access, and Data layers.

The Presentation tier handles everything the user sees and interacts with — JSP pages act as views, Servlets act as controllers, and a SecurityFilter sits in front of all of it. The Business tier is a set of stateless EJBs that hold the core logic and handle email notifications. Below that, the Data Access tier uses the DAO pattern for all database operations, talking to a MySQL database through a DBCP connection pool. One external dependency worth calling out: an SMTP server for automated emails sent throughout the recovery process.

## UML Diagrams

The system design is documented across five diagrams.

![Component Diagram](diagrams/components.png)

Figure 1: Component diagram showing web components, Servlets, EJBs, and DAOs.

![Entity Relationship Diagram](diagrams/erd.png)

Figure 2: ER diagram showing the database schema and table relationships.

![Login Authentication Flow](diagrams/login-flow.png)

Figure 3: Sequence diagram for the login authentication flow.

![Recovery Plan Creation Flow](diagrams/recovery-plan-flow.png)

Figure 4: Sequence diagram for recovery plan creation.

![Navigation Flow](diagrams/navigation-flow.png)

Figure 5: Activity diagram showing page navigation for both user roles.

## Tier Diagram

![System Architecture](diagrams/architecture.png)

Figure 6: System architecture showing the four tiers and external services.

## Architecture and Interconnection of Tiers

A browser request hits the SecurityFilter first, no exceptions. The filter checks the session for an authenticated user and validates the role before anything else in the application runs. If that passes, the request reaches a Servlet. Servlets don't do much heavy lifting themselves — they parse the HTTP request, call the appropriate EJB method, then forward to a JSP for rendering.

The EJBs are where the actual logic lives: CGPA calculations, eligibility checks, recovery plan coordination, and deciding when to trigger email notifications. Below them, DAOs handle every database operation through a DBCP connection pool, using prepared statements throughout.

![Tier Interconnection](diagrams/tier-interconnection.png)

Figure 7: Detailed view of request flow through each tier.

The MySQL database persists all application data including user accounts, student records, grades, recovery plans, and password reset tokens. Email notifications are sent via the configured SMTP server for account management, password recovery, and course recovery updates.

# Database Design

The application uses a MySQL database named crs_db containing six tables.

## Tables

### Users

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| role | VARCHAR(20) | NOT NULL |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| status | VARCHAR(20) | DEFAULT 'active' |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

Stores system user accounts for administrators and academic officers. The role column accepts either 'admin' or 'officer', which determines what the user can access. Passwords are stored as BCrypt hashes — 255 characters accommodates the hash length comfortably. Both username and email carry unique constraints. Status is either 'active' or 'inactive'; deactivated accounts can't log in.

### Students

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| program | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| current_cgpa | DECIMAL(3,2) | DEFAULT 0.00 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

Stores student demographic and academic information. The program column holds the student's academic program, such as Computer Science or Information Technology. The current_cgpa column is calculated by AcademicEJB and stored here for fast retrieval — it gets updated whenever grades change.

### Courses

| Column | Type | Constraints |
|--------|------|-------------|
| code | VARCHAR(10) | PRIMARY KEY |
| title | VARCHAR(200) | NOT NULL |
| credit_hours | INT | NOT NULL |

The course catalog. Course code is the primary key, with values like CS101 or MATH201. This table is referenced by Grades and RecoveryPlans as a foreign key.

### Grades

| Column | Type | Constraints |
|--------|------|-------------|
| student_id | INT | PRIMARY KEY, FK → Students |
| course_code | VARCHAR(10) | PRIMARY KEY, FK → Courses |
| attempt_no | INT | PRIMARY KEY, DEFAULT 1 |
| semester | VARCHAR(20) | NOT NULL |
| year | INT | NOT NULL |
| grade | VARCHAR(5) | NOT NULL |
| grade_point | DECIMAL(3,2) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |

The composite primary key across student_id, course_code, and attempt_no is what allows the system to record up to three attempts per course — a core requirement of the recovery program. Both foreign keys cascade on delete. The grade column accepts A, A-, B+, B, B-, C+, C, C-, D+, D, and F. Grade point maps from 4.0 for an A down to 0.0 for an F. Status is Pass for grades D and above, Fail for F.

### RecoveryPlans

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| student_id | INT | FK → Students |
| course_code | VARCHAR(10) | FK → Courses |
| task | TEXT | NOT NULL |
| deadline | TIMESTAMP | NOT NULL |
| status | VARCHAR(20) | DEFAULT active |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

Stores recovery plan entries for students who need to retake courses. Status accepts active, completed, or cancelled. Both foreign keys cascade on delete. When a recovery plan is created or its status changes, RecoveryEJB sends an email notification to the student automatically.

### PasswordResetTokens

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INT | FK → Users |
| token | VARCHAR(255) | UNIQUE, NOT NULL |
| expires_at | TIMESTAMP | NOT NULL |
| used | BOOLEAN | DEFAULT FALSE |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

Stores secure password reset tokens generated by UserEJB. Tokens expire one hour from creation. The used flag is checked before allowing any reset — a token that's been used once is dead even if it technically hasn't expired yet. The token column carries a unique constraint, and the FK to Users cascades on delete.

## ER Diagram

![Entity Relationship Diagram](diagrams/erd.png)

Figure 8: Full database schema with table relationships.

The ER diagram shows all six tables and their relationships. Users has a one-to-many relationship with PasswordResetTokens. Students has one-to-many relationships with Grades and RecoveryPlans. Courses has one-to-many relationships with Grades and RecoveryPlans.

Performance indexes are created on columns that get queried frequently: username and role in Users; name and program in Students; student_id and status in Grades; student_id, course_code, and status in RecoveryPlans; token, user_id, and expires_at in PasswordResetTokens.

## Database Access APIs

The DAO pattern provides database access abstraction through six DAO classes. All of them get connections from DBConnect, which wraps an Apache Commons DBCP connection pool. Every query uses prepared statements — no raw string concatenation, so SQL injection is not a concern.

UserDAO handles user account operations: create, findByUsername, update, delete, getAll, and getUserById.

StudentDAO handles student data: getAllStudents, getStudentById, create, update, calculateCGPA, getFailedCourseCount, and getStudentGrades.

RecoveryDAO handles recovery plan data: save, getByStudentId, updateStatus, and delete.

AnalyticsDAO handles aggregated reporting data: getSystemAnalytics, getCgpaDistribution, getGradeDistribution, and getFailedCoursesBySemester.

PasswordResetDAO handles token operations: saveToken, findToken, markAsUsed, and deleteExpiredTokens.

## Conclusion

The Course Recovery System demonstrates a practical implementation of Jakarta EE web application architecture for educational institutions. The system successfully addresses the core requirements of managing student academic performance and coordinating course recovery programs through a well-structured multi-tier architecture.

### Key Achievements

The application implements a clean separation of concerns across four distinct tiers. The Presentation Tier uses JSP for views and Servlets for controllers, with a Security Filter handling authentication and authorization centrally. The Business Tier encapsulates all business logic in Enterprise JavaBeans, providing transaction management and email notification services. The Data Access Tier uses the DAO pattern to abstract database operations, ensuring maintainability and testability. The Data Tier leverages MySQL with appropriate indexing for query performance.

Security is implemented at multiple levels. Session-based authentication with BCrypt password hashing protects user credentials. CSRF token validation prevents cross-site request forgery attacks on all POST operations. Role-based access control ensures that admin users have access to user management and academic reporting while officer users have full system access including recovery plan management. The Security Filter intercepts all requests and enforces these policies consistently.

The database design supports the three-attempt policy that is central to the recovery program. The composite primary key in the Grades table allows tracking of multiple attempts per course. The RecoveryPlans table provides a simple mechanism for officers to create and monitor recovery tasks. The PasswordResetTokens table enables secure password recovery with time-limited tokens.

Email notifications keep students informed throughout their recovery journey. Automated messages are sent for account creation, password resets, recovery plan assignments, and academic report delivery. All email templates include relevant details such as course codes, deadlines, and task descriptions.

### Technical Decisions

The choice of Jakarta EE over newer frameworks like Spring Boot reflects the educational context and the need to demonstrate enterprise Java standards. The use of EJB for business logic provides automatic transaction demarcation without additional configuration. The DAO pattern was selected over JPA or Hibernate to maintain explicit control over SQL queries and demonstrate fundamental database access patterns.

The decision to use PlantUML for all diagrams ensures consistency and maintainability. Diagrams can be regenerated whenever the source files change, and the text-based format works well with version control systems.

### Limitations and Future Enhancements

The current implementation has several areas for potential improvement. The user interface uses basic JSP with minimal CSS styling. A modern frontend framework like React or Vue.js could provide a more responsive user experience. The application lacks input validation on the client side, relying entirely on server-side validation.

The recovery plan system uses a simple task-based approach. Future versions could implement the previously designed Milestones and ActionPlans tables for more granular tracking of student progress. Integration with student information systems via APIs would enable automatic grade imports and program enrollment verification.

The email system uses synchronous sending, which blocks request processing. A message queue or scheduled job approach would improve responsiveness. Email templates are currently hardcoded in Java strings. External template files or a template engine like Thymeleaf would improve maintainability.

The application does not implement audit logging for administrative actions. Adding an audit trail would help track user management changes and recovery plan modifications. Session management could be enhanced with remember-me functionality and multi-device session tracking.

### Deployment Considerations

Production deployment requires several configuration changes. The database connection pool should be tuned based on expected concurrent users. SMTP credentials must be configured with a production mail server. Session timeout values should balance security with user convenience. HTTPS should be enabled for all traffic, and the secure flag should be set on session cookies.

Backup procedures should include daily database dumps and periodic verification of restore procedures. Log rotation prevents disk space exhaustion from application logs. Monitoring should track response times, error rates, and database connection pool utilization.

### Summary

The Course Recovery System provides a functional platform for managing student academic recovery programs. The architecture follows established Jakarta EE patterns and demonstrates proper separation of concerns. Security measures protect user data and prevent common web vulnerabilities. The database design supports the core business requirements of grade tracking and recovery plan management. Email notifications maintain communication with students throughout their recovery process.

The system serves as a foundation that can be extended with additional features such as advanced analytics, mobile applications, or integration with institutional systems. The modular architecture allows individual components to be enhanced or replaced without affecting the overall system structure.
