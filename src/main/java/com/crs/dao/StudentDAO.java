package com.crs.dao;

import com.crs.model.Student;
import com.crs.model.Grade;
import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private static final String INSERT_STUDENT = "INSERT INTO Students (name, program, email, current_cgpa) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STUDENT = "UPDATE Students SET name = ?, program = ?, email = ?, current_cgpa = ? WHERE id = ?";
    private static final String SELECT_STUDENT_BY_ID = "SELECT * FROM Students WHERE id = ?";
    private static final String SELECT_ALL_STUDENTS = "SELECT * FROM Students";
    private static final String SELECT_FAILED_COURSES = "SELECT g.*, c.title, c.credit_hours FROM Grades g JOIN Courses c ON g.course_code = c.code WHERE g.student_id = ? AND g.status = 'failed'";
    private static final String SELECT_STUDENT_GRADES = "SELECT g.*, c.title, c.credit_hours FROM Grades g JOIN Courses c ON g.course_code = c.code WHERE g.student_id = ? ORDER BY g.year DESC, g.semester DESC";

    public static void addStudent(Student student) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_STUDENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getProgram());
            ps.setString(3, student.getEmail());
            ps.setDouble(4, student.getCurrentCgpa());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateStudent(Student student) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STUDENT)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getProgram());
            ps.setString(3, student.getEmail());
            ps.setDouble(4, student.getCurrentCgpa());
            ps.setInt(5, student.getId());
            ps.executeUpdate();
        }
    }

    public static Student getStudentById(int studentId) throws SQLException, IOException {
        Student student = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_STUDENT_BY_ID)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setProgram(rs.getString("program"));
                    student.setEmail(rs.getString("email"));
                    student.setCurrentCgpa(rs.getDouble("current_cgpa"));
                }
            }
        }
        return student;
    }

    public static List<Student> getAllStudents() throws SQLException, IOException {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_STUDENTS)) {
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setProgram(rs.getString("program"));
                student.setEmail(rs.getString("email"));
                student.setCurrentCgpa(rs.getDouble("current_cgpa"));
                students.add(student);
            }
        }
        return students;
    }

    public static List<Grade> getFailedCourses(int studentId) throws SQLException, IOException {
        List<Grade> grades = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_FAILED_COURSES)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setStudentId(rs.getInt("student_id"));
                    grade.setCourseCode(rs.getString("course_code"));
                    grade.setSemester(rs.getString("semester"));
                    grade.setYear(rs.getInt("year"));
                    grade.setAttemptNo(rs.getInt("attempt_no"));
                    grade.setGrade(rs.getString("grade"));
                    grade.setGradePoint(rs.getDouble("grade_point"));
                    grade.setStatus(rs.getString("status"));
                    grades.add(grade);
                }
            }
        }
        return grades;
    }

    public static List<Grade> getStudentGrades(int studentId) throws SQLException, IOException {
        List<Grade> grades = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_STUDENT_GRADES)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setStudentId(rs.getInt("student_id"));
                    grade.setCourseCode(rs.getString("course_code"));
                    grade.setSemester(rs.getString("semester"));
                    grade.setYear(rs.getInt("year"));
                    grade.setAttemptNo(rs.getInt("attempt_no"));
                    grade.setGrade(rs.getString("grade"));
                    grade.setGradePoint(rs.getDouble("grade_point"));
                    grade.setStatus(rs.getString("status"));
                    grades.add(grade);
                }
            }
        }
        return grades;
    }

    public static double calculateCGPA(int studentId) throws SQLException, IOException {
        String query = "SELECT SUM(g.grade_point * c.credit_hours) / SUM(c.credit_hours) as cgpa " +
                       "FROM Grades g JOIN Courses c ON g.course_code = c.code " +
                       "WHERE g.student_id = ? AND g.status = 'passed'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("cgpa");
                }
            }
        }
        return 0.0;
    }

    public static int getFailedCourseCount(int studentId) throws SQLException, IOException {
        String query = "SELECT COUNT(DISTINCT course_code) as count FROM Grades WHERE student_id = ? AND status = 'failed'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
}
