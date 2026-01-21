INSERT INTO Users (username, password, role, email, status) VALUES
('admin', 'admin123', 'admin', 'admin@crs.edu', 'active'),
('officer1', 'officer123', 'officer', 'officer1@crs.edu', 'active'),
('officer2', 'officer123', 'officer', 'officer2@crs.edu', 'active');

INSERT INTO Students (id, name, program, email, current_cgpa) VALUES
(1, 'John Doe', 'Bachelor of Computer Science', 'john.doe@crs.edu', 3.25),
(2, 'Jane Smith', 'Bachelor of Information Technology', 'jane.smith@crs.edu', 2.85),
(3, 'Alex Tan', 'Bachelor of Computer Science', 'alex.tan@crs.edu', 3.11),
(4, 'Sarah Johnson', 'Bachelor of Software Engineering', 'sarah.johnson@crs.edu', 1.95),
(5, 'Michael Brown', 'Bachelor of Computer Science', 'michael.brown@crs.edu', 2.45),
(6, 'Emily Davis', 'Bachelor of Information Technology', 'emily.davis@crs.edu', 3.55),
(7, 'David Wilson', 'Bachelor of Computer Science', 'david.wilson@crs.edu', 1.75);

INSERT INTO Courses (code, title, credit_hours) VALUES
('CS201', 'Data Structures', 3),
('CS205', 'Database Systems', 3),
('CS210', 'Software Engineering I', 3),
('MA202', 'Discrete Mathematics', 4),
('EN201', 'Academic Writing', 2),
('CS301', 'Algorithms', 4),
('CS305', 'Web Development', 3),
('CS310', 'Operating Systems', 4),
('CS315', 'Computer Networks', 3),
('MA301', 'Linear Algebra', 3);

INSERT INTO Grades (student_id, course_code, semester, year, attempt_no, grade, grade_point, status) VALUES
(1, 'CS201', 'Semester 1', 2024, 1, 'A', 4.0, 'passed'),
(1, 'CS205', 'Semester 1', 2024, 1, 'B+', 3.3, 'passed'),
(1, 'CS210', 'Semester 1', 2024, 1, 'B', 3.0, 'passed'),
(1, 'MA202', 'Semester 1', 2024, 1, 'C+', 2.3, 'passed'),
(1, 'EN201', 'Semester 1', 2024, 1, 'A-', 3.7, 'passed'),

(2, 'CS201', 'Semester 1', 2024, 1, 'B+', 3.3, 'passed'),
(2, 'CS205', 'Semester 1', 2024, 1, 'B', 3.0, 'passed'),
(2, 'CS210', 'Semester 1', 2024, 1, 'B-', 2.7, 'passed'),
(2, 'MA202', 'Semester 1', 2024, 1, 'B', 3.0, 'passed'),
(2, 'EN201', 'Semester 1', 2024, 2, 'C', 2.0, 'passed'),

(3, 'CS201', 'Semester 1', 2024, 1, 'A', 4.0, 'passed'),
(3, 'CS205', 'Semester 1', 2024, 1, 'B+', 3.3, 'passed'),
(3, 'CS210', 'Semester 1', 2024, 1, 'B', 3.0, 'passed'),
(3, 'MA202', 'Semester 1', 2024, 1, 'C+', 2.3, 'passed'),
(3, 'EN201', 'Semester 1', 2024, 1, 'A-', 3.7, 'passed'),

(4, 'CS201', 'Semester 1', 2024, 1, 'C', 2.0, 'passed'),
(4, 'CS205', 'Semester 1', 2024, 2, 'D+', 1.3, 'failed'),
(4, 'CS210', 'Semester 1', 2024, 1, 'C-', 1.7, 'passed'),
(4, 'MA202', 'Semester 1', 2024, 2, 'F', 0.0, 'failed'),
(4, 'EN201', 'Semester 1', 2024, 1, 'D', 1.0, 'failed'),

(5, 'CS201', 'Semester 1', 2024, 1, 'B-', 2.7, 'passed'),
(5, 'CS205', 'Semester 1', 2024, 1, 'B', 3.0, 'passed'),
(5, 'CS210', 'Semester 1', 2024, 1, 'C+', 2.3, 'passed'),
(5, 'MA202', 'Semester 1', 2024, 1, 'C', 2.0, 'passed'),
(5, 'EN201', 'Semester 1', 2024, 2, 'D+', 1.3, 'failed'),

(6, 'CS201', 'Semester 1', 2024, 1, 'A', 4.0, 'passed'),
(6, 'CS205', 'Semester 1', 2024, 1, 'A-', 3.7, 'passed'),
(6, 'CS210', 'Semester 1', 2024, 1, 'A', 4.0, 'passed'),
(6, 'MA202', 'Semester 1', 2024, 1, 'A-', 3.7, 'passed'),
(6, 'EN201', 'Semester 1', 2024, 1, 'A', 4.0, 'passed'),

(7, 'CS201', 'Semester 1', 2024, 1, 'D', 1.0, 'failed'),
(7, 'CS205', 'Semester 1', 2024, 1, 'D+', 1.3, 'failed'),
(7, 'CS210', 'Semester 1', 2024, 2, 'F', 0.0, 'failed'),
(7, 'MA202', 'Semester 1', 2024, 2, 'F', 0.0, 'failed'),
(7, 'EN201', 'Semester 1', 2024, 1, 'D+', 1.3, 'failed');

INSERT INTO RecoveryPlans (student_id, course_code, task, deadline, status) VALUES
(4, 'CS205', 'Complete additional database exercises and submit resit exam', '2025-02-15 23:59:59', 'active'),
(4, 'MA202', 'Attend remedial classes and complete all assignments', '2025-02-20 23:59:59', 'active'),
(4, 'EN201', 'Resubmit essay with corrections', '2025-02-10 23:59:59', 'active'),
(5, 'EN201', 'Complete additional writing exercises and resubmit', '2025-02-25 23:59:59', 'active'),
(7, 'CS201', 'Complete all tutorial exercises and resit exam', '2025-03-01 23:59:59', 'active'),
(7, 'CS205', 'Attend extra lab sessions and complete all labs', '2025-03-05 23:59:59', 'active'),
(7, 'CS210', 'Complete project and submit for 3rd attempt', '2025-03-10 23:59:59', 'active');
