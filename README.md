# Course Recovery System
Note: nearly 100% vibe coded for now. But we'll manually review.

A Java EE web application for managing course recovery plans in educational institutions.

## tech stack
- **Java:** JDK 17+
- **Application Server:** Apache TomEE 9.x (Jakarta EE 9)
- **Database:** MySQL 8.0
- **Build Tool:** Maven 3.8+
- **Presentation Layer:** JSP, Servlets, JSTL
- **Business Layer:** EJBs
- **Data Access:** JDBC with DAO pattern
- **Containerization:** Docker or Podman

## Prerequisites
- JDK 17 or higher
- Maven 3.8 or higher
- For containerized deployment:
  - Docker or Podman
- For manual deployment:
  - MySQL
  - TomEE

## deployment
### deploy with podman
to start:
```bash
mvn clean package && podman-compose up -d --force-recreate
# application should now be accessible in http://localhost:8080/
```

to restart:
```bash
mvn package && podman-compose restart tomee
# use mvn clean package if there are weird issues
# restarting tomee shouldn't be needed to reload, but for some reason it doesn't
# unfortunately restarting tomee logs you out too
```

to view stdout logs:
```bash
podman-compose logs -f
```

to stop:
```bash
# Stop containers
podman-compose down

# Stop and remove volumes (clean slate)
podman-compose down -v
```

### manual deployment
```bash
# 1. Start MySQL
mysql -u root -p < Database_Scripts/schema.sql
mysql -u root -p crs_db < Database_Scripts/data.sql

# 2. Configure environment variables:
export DB_URL="jdbc:mysql://localhost:3306/crs_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="rootpassword"

# 3. Deploy WAR to TomEE
cp target/course-recovery-system.war $TOMEE_HOME/webapps/

# 4. Start TomEE
$TOMEE_HOME/bin/startup.sh

# Access at: http://localhost:8080/course-recovery-system/
```

## Troubleshooting
### MySQL Connection Issues
```bash
# Check if MySQL is running
podman ps | grep mysql

# Check MySQL logs
podman logs crs-mysql

# Access MySQL directly
mysql -h localhost -P 3306 -u crs_user -p crs_db
```

### TomEE Deployment Issues
```bash
# Check if TomEE is running
podman ps | grep tomee

# Check TomEE logs
podman logs crs-tomee

# Verify WAR file exists
ls -lh target/course-recovery-system.war
```

### Email Issues
Email functionality requires valid SMTP credentials in enviroment variables.

## Default Credentials

### Admin User
- **Username:** admin
- **Password:** admin123
- **Role:** Course Administrator

### Academic Officers
- **Username:** officer1
- **Password:** officer123
- **Username:** officer2
- **Password:** officer123

### Database Access
- **Host:** localhost:3306 (or mysql:3306 in container)
- **Database:** crs_db
- **User:** crs_user / crs_password
- **Root:** root / rootpassword

## Project Structure

```
src/
├── main/
│   ├── java/com/crs/
│   │   ├── model/         # Entity classes (User, Student, Course, Grade, RecoveryPlan)
│   │   ├── ejb/           # Business logic (UserEJB, AcademicEJB, RecoveryEJB)
│   │   ├── servlet/        # Web controllers (AuthServlet, AdminServlet, OfficerServlet)
│   │   ├── dao/           # Data access objects (UserDAO, StudentDAO, RecoveryDAO)
│   │   └── util/          # Utilities (DBConnect, EmailUtil)
│   └── webapp/
│       ├── WEB-INF/web.xml  # Deployment descriptor
│       ├── css/style.css    # Stylesheets
│       ├── login.jsp
│       ├── error.jsp
│       ├── admin/           # Admin pages
│       └── officer/         # Academic officer pages
Database_Scripts/                 # Database schema and sample data
├── schema.sql
└── data.sql
```

## Architecture

```
┌───────────────────────────────┐
│     Presentation Tier         │
│  (JSP, Servlets, JSTL)        │
└──────────────┬────────────────┘
               │
┌──────────────▼────────────────┐
│      Business Tier            │
│         (EJBs)                │
└──────────────┬────────────────┘
               │
┌──────────────▼────────────────┐
│     Database Tier             │
│       (MySQL 8.0)             │
└───────────────────────────────┘
```

## Features

### Course Administrator
- User account management (add, update, deactivate)
- Role-based access control

### Academic Officer
- **Recovery Plans:** Create, view, update status, delete
- **Eligibility Check:** Verify CGPA ≥ 2.0 and ≤ 3 failed courses
- **Academic Reports:** Generate and email student performance reports
- **Student Management:** View all students and their grades

### Email Notifications
- Welcome emails on account creation
- Recovery plan notifications
- Status update alerts
- Academic report delivery

## Testing
See [`plans/testing.md`](plans/testing.md) for comprehensive test plan.

## Database Schema
### Users
- `id` (PK), `username`, `password`, `role`, `email`, `status`

### Students
- `id` (PK), `name`, `program`, `email`, `current_cgpa`

### Courses
- `code` (PK), `title`, `credit_hours`

### Grades
- `student_id` (FK), `course_code` (FK), `semester`, `year`, `attempt_no`, `grade`, `grade_point`, `status`

### RecoveryPlans
- `id` (PK), `student_id` (FK), `course_code` (FK), `task`, `deadline`, `status`
