CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    program VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    current_cgpa DECIMAL(3, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Courses (
    code VARCHAR(10) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    credit_hours INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Grades (
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    attempt_no INT NOT NULL DEFAULT 1,
    grade VARCHAR(5) NOT NULL,
    grade_point DECIMAL(3, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (student_id, course_code, attempt_no),
    FOREIGN KEY (student_id) REFERENCES Students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES Courses(code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS RecoveryPlans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    task TEXT NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES Courses(code) ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON Users(username);
CREATE INDEX idx_users_role ON Users(role);
CREATE INDEX idx_students_name ON Students(name);
CREATE INDEX idx_students_program ON Students(program);
CREATE INDEX idx_grades_student ON Grades(student_id);
CREATE INDEX idx_grades_status ON Grades(status);
CREATE INDEX idx_recovery_student ON RecoveryPlans(student_id);
CREATE INDEX idx_recovery_status ON RecoveryPlans(status);
