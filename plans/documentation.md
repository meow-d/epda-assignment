# Course Recovery System - Technical Documentation

## Overview of Application

### System Architecture

The Course Recovery System is a web-based application built using Jakarta EE technologies for managing student academic performance and course recovery programs. The system follows a multi-tier architecture pattern with clear separation of concerns across presentation, business, data access, and data tiers.

The architecture consists of four primary tiers. The Presentation Tier contains JSP pages for views and Servlets for controllers, with a Security Filter handling cross-cutting concerns. The Business Tier comprises Enterprise JavaBeans that encapsulate business logic and handle email notifications. The Data Access Tier uses the DAO pattern for database operations. The Data Tier is implemented using MySQL relational database.

External services include an SMTP mail server for sending automated email notifications to users.

![System Architecture](diagrams/architecture.png)

Figure 1: System Architecture - Shows the four-tier architecture with external services

### Tier Interconnection

![Tier Interconnection](diagrams/tier-interconnection.png)

Figure 2: Tier Interconnection - Details the flow of requests through each tier

The application processes requests through the tiers in a sequential manner. Client requests from a web browser first encounter the Security Filter which intercepts all incoming requests. The filter validates authentication status and checks role-based authorization before allowing access to Servlets.

Servlets act as controllers that process HTTP requests and invoke appropriate business logic methods on EJB components. The EJB layer executes business rules, performs calculations, and initiates email notifications when required. Data Access Objects handle all database operations using JDBC connections from a connection pool.

The MySQL database persists all application data including user accounts, student records, grades, recovery plans, milestones, and action plans. Email notifications are sent via the configured SMTP server for account management, password recovery, and course recovery updates.

### UML Diagrams

The system design is documented using multiple UML diagrams. The component diagram shows the relationships between web components, servlets, EJBs, and DAOs. The entity relationship diagram illustrates the database schema with table relationships. Sequence diagrams document the login authentication flow and recovery plan creation process. An activity diagram shows the page navigation flow for both user roles.

![Component Diagram](diagrams/components.png)

Figure 3: Component Diagram - Shows web components, servlets, EJBs, and DAOs

![Entity Relationship Diagram](diagrams/erd.png)

Figure 4: Entity Relationship Diagram - Shows database tables and their relationships

![Login Authentication Flow](diagrams/login-flow.png)

Figure 5: Login Authentication Flow - Sequence diagram showing the login process

![Navigation Flow](diagrams/navigation-flow.png)

Figure 6: Navigation Flow - Activity diagram showing page navigation for both roles

![Recovery Plan Creation Flow](diagrams/recovery-plan-flow.png)

Figure 7: Recovery Plan Creation Flow - Sequence diagram showing recovery plan creation

---

## Design of Web Components

### JSP Implementation

JavaServer Pages serve as the view layer of the application using JSTL tags for dynamic content rendering. All JSP files are stored under the WEB-INF directory to prevent direct access, ensuring all requests pass through the Security Filter.

The JSP pages are organized into two role-based directories. The admin directory contains eight pages including dashboard, user management forms, advanced reports with analytics charts, eligibility checking interface, and academic report generation. The officer directory contains two pages for the officer dashboard and recovery plan management.

Reusable components are stored in the includes directory. The header.jsp file provides unified navigation with role-based menu items using JSTL choose tags. The footer.jsp file provides a consistent footer across all pages.

The JSP implementation follows a consistent pattern. Each page sets the currentPage attribute for navigation highlighting, includes the header using jsp:include tags with proper XML self-closing syntax, renders page-specific content using JSTL core tags for iteration and conditionals, and includes the footer.

### Servlet Implementation

Servlets function as controller components in the Model-View-Controller pattern, handling HTTP requests and coordinating between the view and business layers. All servlets use annotation-based configuration with the @WebServlet annotation.

The AuthServlet handles user authentication at the /auth/* URL pattern. It processes login requests with BCrypt password verification, manages session creation, handles logout by invalidating sessions, and processes password reset requests. The servlet redirects users to role-appropriate dashboards after successful authentication.

The AdminServlet is mapped to /admin/* and handles user management operations including listing users, creating new users, editing existing users, and deleting users. It also handles eligibility checking and academic report generation for students. The servlet validates that the session contains either admin or officer role before processing requests.

The OfficerServlet at /officer/* handles recovery plan management including viewing existing plans, creating new recovery plans, updating plan status, and deleting plans. It also manages milestones and action plans associated with recovery programs.

The AdvancedReportServlet provides analytics data for the admin dashboard, retrieving system-wide statistics and academic analytics from the AnalyticsEJB. It serializes data to JSON format using Jackson for consumption by Chart.js visualizations.

The ExportCsvServlet generates CSV file downloads of analytics data with timestamped filenames. It supports exporting system analytics, academic analytics, or complete data sets.

### Filter Implementation

The SecurityFilter implements cross-cutting security concerns and is mapped to intercept all requests using the /* URL pattern. The filter performs authentication validation by checking for the user attribute in the HTTP session.

Authorization is enforced through role-based access control. The admin role has access to the /admin/* URL space for user management and reports. The officer role has full access to both /admin/* and /officer/* URL spaces, reflecting the use case requirement that officers have all admin capabilities plus recovery plan management.

CSRF protection is implemented for all POST requests. The filter validates the csrfToken parameter against the token stored in the session. Requests with missing or invalid tokens are rejected with HTTP 403 Forbidden.

The filter handles special cases for logged-in users. Requests to /login.jsp from authenticated users result in redirection to the role-appropriate dashboard. The root path / redirects authenticated users to their dashboard while unauthenticated users see the login page.

Static resources under /css/, /js/, and /images/ paths bypass authentication checks. The /auth/* path is accessible for login and logout operations. Password reset pages are publicly accessible to allow users to recover forgotten passwords.

---

## Web Page Design

### Navigation Chart

The application implements role-based navigation with two distinct user roles having different access levels. The admin role provides access to user management and academic reporting features. The officer role includes all admin capabilities plus additional recovery plan management functionality.

Unauthenticated users accessing any protected resource are redirected to the login page. The login page displays a form with username, password, and CSRF token fields. Upon successful authentication, users are redirected based on their role. Admin users are directed to the admin dashboard at /admin/. Officer users are directed to the officer dashboard at /officer/.

Authenticated users attempting to access the login page are automatically redirected to their role dashboard, preventing unnecessary re-authentication. The logout action destroys the session and redirects to the login page.

From the admin dashboard, users can navigate to manage users for viewing and editing user accounts, add user for creating new accounts, advanced reports for analytics visualization, eligibility check for determining student program eligibility, and academic report for generating individual student performance reports.

From the officer dashboard, users can navigate to recovery plans for managing student recovery programs including creating milestones and action plans. Officers can also access all admin pages due to their elevated privileges.

![Navigation Flow](diagrams/navigation-flow.png)

Figure 8: Navigation Flow - Activity diagram showing page navigation paths for admin and officer roles

### Page Descriptions

#### Public Pages

The login.jsp page provides the authentication interface with username and password fields, CSRF token protection, and links to password recovery. Error messages display for failed authentication attempts.

The forgotPassword.jsp page allows users to request password reset by entering their email address. A confirmation message indicates that a reset link has been sent if the email exists in the system.

The resetPassword.jsp page accepts a reset token from the email link and allows users to enter a new password. Token validation ensures the reset link is valid and not expired.

The error.jsp page displays error messages and stack traces for application errors, with appropriate HTTP status codes.

#### Admin Pages

The admin index.jsp dashboard displays system statistics including total students, system users, eligible and ineligible student counts. Quick links provide access to common operations.

The manageUsers.jsp page lists all users in a table with username, role, email, and status columns. Action buttons allow editing or deactivating users.

The addUser.jsp page provides a form for creating new user accounts with fields for username, password, role selection, and email. Validation ensures unique usernames and valid email format.

The editUser.jsp page pre-populates a form with existing user data for modification. Password change is optional.

The advancedReports.jsp page displays analytics visualizations using Chart.js including CGPA distribution pie chart, grade distribution bar chart, and failed courses by semester line chart. Export buttons allow downloading data as CSV or printing as PDF.

The eligibility.jsp page shows a table of all students with calculated CGPA, failed course count, and eligibility status. Students are eligible if CGPA is at least 2.0 and failed courses do not exceed three.

The academicReport.jsp page allows selecting a student and viewing their complete grade history with calculated CGPA. A button sends the report via email to the student.

#### Officer Pages

The officer index.jsp dashboard lists all students with their current CGPA and recovery plan status. Quick statistics show active recovery plans and pending milestones.

The recoveryPlan.jsp page provides comprehensive recovery plan management. A table shows existing plans with status update and delete actions. A form creates new recovery plans with course code, task description, and deadline. Milestone and action plan sections allow detailed tracking of recovery progress.

---

## Design of Business Tier

### Enterprise JavaBeans

The business tier is implemented using four stateless Enterprise JavaBeans that encapsulate business logic, manage transactions, and coordinate email notifications.

### UserEJB

The UserEJB handles all user-related business operations. The authenticate method accepts username and password parameters, retrieves the user from the database via UserDAO, and verifies the password using BCrypt hashing. A successful authentication returns the User object for session storage.

The createUser method validates input parameters, checks for existing usernames and emails, hashes the password using BCrypt, creates the User entity, persists it via UserDAO, and sends a welcome email notification.

The requestPasswordReset method generates a secure random token, stores it in the PasswordResetTokens table with an expiration time of one hour, and sends a password reset request email containing the reset link.

The resetPasswordWithToken method validates the token exists and is not expired, verifies it has not been used, updates the user password with the BCrypt hashed value, marks the token as used, and sends a password reset confirmation email.

The sendDeactivationEmail method notifies users when their account status changes to inactive.

### AcademicEJB

The AcademicEJB manages academic operations and student performance calculations. The calculateCGPA method retrieves all grades for a student, computes grade points based on the institutional grading scale, and returns the cumulative grade point average rounded to two decimal places.

The isEligibleForRecovery method determines if a student qualifies for the recovery program by checking that CGPA is below 2.0 or failed courses exceed three, but the student has not exceeded maximum attempts.

The getStudentGrades method retrieves all grade records for a student ordered by semester and year. The getFailedCourseCount method returns the count of courses with failing grades.

The canAttemptCourse method enforces the three-attempt policy by counting previous attempts for a course and returning true if fewer than three attempts exist.

The sendAcademicReport method generates a formatted academic performance report containing student information, program details, CGPA, and complete grade history, then emails it to the student.

### RecoveryEJB

The RecoveryEJB coordinates the course recovery program. The createRecoveryPlan method accepts student ID, course code, task description, and deadline, creates a RecoveryPlan entity, persists it via RecoveryDAO, and sends a notification email to the student.

The createMilestone method creates milestone entries for recovery plans with title, description, and target completion date. The createActionPlan method creates action plan entries linked to milestones with specific tasks and deadlines.

The updateMilestoneStatus method changes milestone status and triggers status update email notifications. The updateActionPlanStatus method updates action plan progress and sends progress notifications.

The sendRecoveryPlanEmail method constructs and sends email notifications when recovery plans are created. The sendMilestoneEmail method notifies students of new milestone assignments. The sendActionPlanEmail method notifies students of new action plan tasks.

### AnalyticsEJB

The AnalyticsEJB aggregates system-wide statistics for reporting. The getSystemAnalytics method returns a map containing total student count, total user count, eligible student count, ineligible student count, active recovery plan count, completed recovery plan count, total failed courses count, and recovery success rate percentage.

The getAcademicAnalytics method returns a map containing CGPA distribution by category, grade distribution by letter grade, and failed courses grouped by semester.

The getCgpaDistribution method categorizes students into Fail (below 2.0), Pass (2.0 to 2.49), Satisfactory (2.5 to 2.99), Good (3.0 to 3.49), and Excellent (3.5 and above) categories.

The getGradeDistribution method counts grades by letter grade from A through F including plus and minus variants. The getFailedCoursesBySemester method groups failed courses by semester and year for trend analysis.

### Email Notification System

The application sends automated email notifications using the Jakarta Mail API. The EmailUtil class provides a static sendEmail method that accepts recipient address, subject, and body parameters.

Email configuration is read from environment variables including MAIL_SMTP_HOST, MAIL_SMTP_PORT, MAIL_SMTP_USERNAME, MAIL_SMTP_PASSWORD, MAIL_SMTP_AUTH, and MAIL_SMTP_STARTTLS_ENABLE.

Notifications are sent for user account creation, account deactivation, password reset requests, password reset confirmations, recovery plan creation, milestone assignments, action plan assignments, milestone status updates, action plan progress updates, and academic report delivery.

---

## Database Design

### Tables

The application uses MySQL database named crs_db containing eight tables that store user accounts, student information, course catalog, grade records, recovery plans, milestones, action plans, and password reset tokens.

#### Users Table

The Users table stores system user account information for administrators and academic officers.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| role | VARCHAR(20) | NOT NULL | User role: admin or officer |
| email | VARCHAR(100) | UNIQUE, NOT NULL | User email address |
| status | VARCHAR(20) | DEFAULT active | Account status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation date |

The username column has a unique constraint ensuring no duplicate usernames exist. The email column also has a unique constraint. The password column stores BCrypt hashed passwords with a length of 255 characters to accommodate hash strings. The role column accepts values admin or officer determining system access levels. The status column indicates whether the account is active or inactive.

#### Students Table

The Students table stores student demographic and academic information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique student identifier |
| name | VARCHAR(100) | NOT NULL | Student full name |
| program | VARCHAR(100) | NOT NULL | Academic program |
| email | VARCHAR(100) | UNIQUE, NOT NULL | Student email address |
| current_cgpa | DECIMAL(3,2) | DEFAULT 0.00 | Current cumulative GPA |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation date |

The name column stores the student full name. The program column indicates the academic program such as Computer Science or Information Technology. The email column has a unique constraint. The current_cgpa column stores the calculated cumulative grade point average with precision for two decimal places.

#### Courses Table

The Courses table stores the course catalog information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| code | VARCHAR(10) | PRIMARY KEY | Course code |
| title | VARCHAR(200) | NOT NULL | Course title |
| credit_hours | INT | NOT NULL | Course credit hours |

The code column serves as primary key with values like CS101 or MATH201. The title column contains the full course name. The credit_hours column indicates the course credit value.

#### Grades Table

The Grades table stores student grade records with a composite primary key supporting multiple attempts per course.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| student_id | INT | PRIMARY KEY, FOREIGN KEY | Reference to Students |
| course_code | VARCHAR(10) | PRIMARY KEY, FOREIGN KEY | Reference to Courses |
| attempt_no | INT | PRIMARY KEY, DEFAULT 1 | Attempt number |
| semester | VARCHAR(20) | NOT NULL | Academic semester |
| year | INT | NOT NULL | Academic year |
| grade | VARCHAR(5) | NOT NULL | Letter grade |
| grade_point | DECIMAL(3,2) | NOT NULL | Grade point value |
| status | VARCHAR(20) | NOT NULL | Pass or Fail status |

The composite primary key consists of student_id, course_code, and attempt_no allowing up to three attempts per course as per the three-attempt policy. Foreign key constraints reference the Students and Courses tables with cascade delete. The grade column accepts values A, A-, B+, B, B-, C+, C, C-, D+, D, F. The grade_point column stores the numeric equivalent from 4.0 for A down to 0.0 for F. The status column indicates Pass for grades D and above, Fail for F grade.

#### Milestones Table

The Milestones table stores recovery plan milestones assigned to students.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique milestone identifier |
| student_id | INT | FOREIGN KEY | Reference to Students |
| course_code | VARCHAR(10) | FOREIGN KEY | Reference to Courses |
| title | VARCHAR(200) | NOT NULL | Milestone title |
| description | TEXT | | Milestone description |
| target_date | DATE | NOT NULL | Target completion date |
| status | VARCHAR(20) | DEFAULT active | Milestone status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation date |

Foreign key constraints reference Students and Courses tables with cascade delete. The status column accepts values active, completed, or cancelled. The target_date column specifies when the milestone should be completed.

#### ActionPlans Table

The ActionPlans table stores action plan tasks linked to milestones.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique action plan identifier |
| milestone_id | INT | FOREIGN KEY | Reference to Milestones |
| student_id | INT | FOREIGN KEY | Reference to Students |
| course_code | VARCHAR(10) | FOREIGN KEY | Reference to Courses |
| task | TEXT | NOT NULL | Task description |
| deadline | TIMESTAMP | NOT NULL | Task deadline |
| status | VARCHAR(20) | DEFAULT pending | Task status |
| grade | VARCHAR(5) | | Grade achieved |
| grade_point | DECIMAL(3,2) | | Grade point achieved |
| progress_notes | TEXT | | Progress notes |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation date |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update date |

Foreign key constraints reference Milestones, Students, and Courses tables with cascade delete. The status column accepts values pending, in_progress, completed, or cancelled. The grade and grade_point columns store the result when the action plan involves a course attempt. The updated_at column automatically updates on row modification.

#### RecoveryPlans Table

The RecoveryPlans table is a legacy table maintained for backward compatibility.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique recovery plan identifier |
| student_id | INT | FOREIGN KEY | Reference to Students |
| course_code | VARCHAR(10) | FOREIGN KEY | Reference to Courses |
| task | TEXT | NOT NULL | Recovery task |
| deadline | TIMESTAMP | NOT NULL | Task deadline |
| status | VARCHAR(20) | DEFAULT active | Plan status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation date |

Foreign key constraints reference Students and Courses tables with cascade delete. New development should use the Milestones and ActionPlans tables instead.

#### PasswordResetTokens Table

The PasswordResetTokens table stores secure password reset tokens.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique token identifier |
| user_id | INT | FOREIGN KEY | Reference to Users |
| token | VARCHAR(255) | UNIQUE, NOT NULL | Secure random token |
| expires_at | TIMESTAMP | NOT NULL | Token expiration date |
| used | BOOLEAN | DEFAULT FALSE | Token usage flag |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Token creation date |

Foreign key constraint references Users table with cascade delete. The token column has a unique constraint. The expires_at column is set to one hour from creation. The used flag prevents token reuse.

### ER Diagram

![Entity Relationship Diagram](diagrams/erd.png)

Figure 9: Entity Relationship Diagram showing database schema and table relationships

The ER diagram illustrates the relationships between entities. The Users entity has a one-to-many relationship with PasswordResetTokens. The Students entity has one-to-many relationships with Grades, Milestones, ActionPlans, and RecoveryPlans. The Courses entity has one-to-many relationships with Grades, Milestones, ActionPlans, and RecoveryPlans. The Milestones entity has a one-to-many relationship with ActionPlans.

### Database Indexes

Performance optimization indexes are created on frequently queried columns. User indexes on username and role columns support authentication and authorization queries. Student indexes on name and program columns support search and filtering operations. Grade indexes on student_id and status columns support CGPA calculation and eligibility queries.

Recovery system indexes on student_id, course_code, and status columns for Milestones, ActionPlans, and RecoveryPlans tables support recovery plan queries. Password reset indexes on token, user_id, and expires_at columns support token validation queries.

### Database Access APIs

The Data Access Object pattern provides database access abstraction through seven DAO classes.

#### UserDAO

The UserDAO class provides user data access operations. The create method inserts a new user record and returns the generated ID. The findByUsername method retrieves a user by username for authentication. The update method modifies user information. The delete method removes a user record. The getAll method retrieves all users for listing. The getUserById method retrieves a user by ID for editing.

#### StudentDAO

The StudentDAO class provides student data access operations. The getAllStudents method retrieves all students with their current CGPA. The getStudentById method retrieves a specific student. The create method inserts a new student record. The update method modifies student information. The calculateCGPA method computes CGPA from grade records. The getFailedCourseCount method counts failed courses for a student. The getStudentGrades method retrieves all grades for a student.

#### RecoveryDAO

The RecoveryDAO class provides recovery plan data access. The save method creates or updates a recovery plan. The getByStudentId method retrieves all recovery plans for a student. The updateStatus method changes plan status. The delete method removes a recovery plan.

#### MilestoneDAO

The MilestoneDAO class provides milestone data access. The save method creates or updates a milestone. The getByStudentId method retrieves milestones for a student. The getByCourseCode method retrieves milestones for a course. The updateStatus method changes milestone status. The delete method removes a milestone.

#### ActionPlanDAO

The ActionPlanDAO class provides action plan data access. The save method creates or updates an action plan. The getByMilestoneId method retrieves action plans for a milestone. The getByStudentId method retrieves action plans for a student. The updateStatus method changes action plan status. The updateProgress method updates progress notes and grade.

#### AnalyticsDAO

The AnalyticsDAO class provides analytics data aggregation. The getSystemAnalytics method returns system-wide statistics. The getCgpaDistribution method returns CGPA category counts. The getGradeDistribution method returns grade letter counts. The getFailedCoursesBySemester method returns failed courses grouped by semester.

#### PasswordResetDAO

The PasswordResetDAO class provides password reset token operations. The saveToken method stores a new reset token. The findToken method retrieves a token by value. The markAsUsed method sets the used flag. The deleteExpiredTokens method removes expired tokens.

All DAO classes use the DBConnect utility for database connection management. The DBConnect class initializes an Apache Commons DBCP connection pool with configurable parameters. The getConnection method returns a pooled connection for database operations. All database access uses prepared statements to prevent SQL injection attacks.

---

## Appendix

### Technology Stack

| Layer | Technology |
|-------|------------|
| Web Server | Apache TomEE 10.1.52 |
| Web Framework | Jakarta Servlet 6.0, JSP 3.1 |
| Business Components | Enterprise JavaBeans |
| Database | MySQL 8.0 |
| Connection Pool | Apache Commons DBCP 2.11.0 |
| Security | BCrypt password hashing, CSRF tokens |
| Email | Jakarta Mail 2.1.0 |
| Utilities | Jackson 2.15.2, Commons BeanUtils |

### Role-Based Access Control Matrix

| Feature | Admin | Officer | Unauthenticated |
|---------|-------|---------|-----------------|
| Login | Yes | Yes | No |
| User Management | Yes | Yes | No |
| Eligibility Check | Yes | Yes | No |
| Academic Reports | Yes | Yes | No |
| Advanced Reports | Yes | Yes | No |
| Recovery Plans | No | Yes | No |
| Password Reset | Yes | Yes | Yes |

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| DB_URL | JDBC connection URL | jdbc:mysql://localhost:3306/crs_db |
| DB_USERNAME | Database username | root |
| DB_PASSWORD | Database password | secret |
| MAIL_SMTP_HOST | SMTP server hostname | smtp.example.com |
| MAIL_SMTP_PORT | SMTP server port | 587 |
| MAIL_SMTP_USERNAME | SMTP username | user@example.com |
| MAIL_SMTP_PASSWORD | SMTP password | secret |
| MAIL_SMTP_AUTH | SMTP authentication flag | true |
| MAIL_SMTP_STARTTLS_ENABLE | TLS encryption flag | true |

---

Documentation generated for Course Recovery System version 1.0
