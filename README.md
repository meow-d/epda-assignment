# Course Recovery System

A Java EE web application for managing course recovery plans in educational institutions.

## Technologies

- **Java:** JDK 17+
- **Application Server:** Apache TomEE 9.x (Jakarta EE 9)
- **Database:** MySQL 8.0
- **Build Tool:** Maven 3.8+
- **Presentation Layer:** JSP, Servlets, JSTL
- **Business Layer:** EJBs
- **Data Access:** JDBC with DAO pattern
- **Containerization:** Podman (Docker-compatible)

## Prerequisites

- JDK 17 or higher
- Maven 3.8 or higher
- Podman (or Docker) for containerized deployment
- MySQL client (optional, for direct database access)

## Quick Start

### 1. Clone and Configure

```bash
# Copy environment template and configure
cp .env.template .env

# Edit .env with your credentials:
# - Database URL, user, password
# - SMTP email settings
```

### 2. Build Application

```bash
# Compile and package WAR file
mvn clean package

# Output: target/course-recovery-system.war
```

### 3. Run with Podman (Recommended)

```bash
# Start MySQL and TomEE containers
podman-compose up -d

# View logs
podman-compose logs -f

# Stop containers
podman-compose down

# Stop and remove volumes (clean slate)
podman-compose down -v
```

**Access the application:** http://localhost:8080/course-recovery-system/

### 4. Run with Docker (Alternative)

```bash
# Start MySQL and TomEE containers
docker-compose up -d

# View logs
docker-compose logs -f

# Stop containers
docker-compose down
```

### 5. Manual Deployment (Local TomEE)

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

## Development

### Compile Only

```bash
mvn compile
```

### Run Tests

```bash
mvn test
```

### Clean Build

```bash
mvn clean install
```

### Skip Tests

```bash
mvn clean package -DskipTests
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

### Port Conflicts

If port 8080 is already in use, modify `docker-compose.yml`:

```yaml
tomee:
  ports:
    - "8081:8080"  # Use 8081 instead
```

Then access at: http://localhost:8081/

### Email Issues

Email functionality requires valid SMTP configuration in `.env`. For Gmail:
1. Enable 2-factor authentication
2. Generate an App Password
3. Use App Password in `.env`, not regular password

## Testing

See [`plans/testing.md`](plans/testing.md) for comprehensive test plan.

### Quick Smoke Test

```bash
# 1. Start services
podman-compose up -d

# 2. Wait for services to be ready (~30 seconds)

# 3. Test login
curl -X POST http://localhost:8080/course-recovery-system/auth/login \
  -d "username=admin&password=admin123"

# 4. Open browser
# Navigate to: http://localhost:8080/course-recovery-system/login.jsp
```

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

## References

- [Jakarta EE Documentation](https://jakarta.ee/)
- [TomEE Documentation](https://tomee.apache.org/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Maven Documentation](https://maven.apache.org/guides/)

## License

Educational project for EPDA assignment.
