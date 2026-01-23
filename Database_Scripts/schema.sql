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

-- Enhanced recovery plan system with milestones and action plans
CREATE TABLE IF NOT EXISTS Milestones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    target_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES Courses(code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ActionPlans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    milestone_id INT,
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    task TEXT NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    grade VARCHAR(5),
    grade_point DECIMAL(3, 2),
    progress_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (milestone_id) REFERENCES Milestones(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES Courses(code) ON DELETE CASCADE
);

-- Keep legacy RecoveryPlans table for backward compatibility (can be removed later)
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
-- Indexes for enhanced recovery system
CREATE INDEX idx_milestones_student ON Milestones(student_id);
CREATE INDEX idx_milestones_course ON Milestones(course_code);
CREATE INDEX idx_milestones_status ON Milestones(status);
CREATE INDEX idx_action_plans_milestone ON ActionPlans(milestone_id);
CREATE INDEX idx_action_plans_student ON ActionPlans(student_id);
CREATE INDEX idx_action_plans_course ON ActionPlans(course_code);
CREATE INDEX idx_action_plans_status ON ActionPlans(status);

-- Legacy indexes
-- Password reset tokens for secure password recovery
CREATE TABLE IF NOT EXISTS PasswordResetTokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_token ON PasswordResetTokens(token);
CREATE INDEX idx_password_reset_user ON PasswordResetTokens(user_id);
CREATE INDEX idx_password_reset_expires ON PasswordResetTokens(expires_at);

-- Legacy indexes
CREATE INDEX idx_recovery_student ON RecoveryPlans(student_id);
CREATE INDEX idx_recovery_status ON RecoveryPlans(status);
