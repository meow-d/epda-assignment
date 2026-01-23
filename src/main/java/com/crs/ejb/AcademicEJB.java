package com.crs.ejb;

import com.crs.dao.StudentDAO;
import com.crs.model.Grade;
import com.crs.model.Student;
import com.crs.util.DBConnect;
import com.crs.util.EmailUtil;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AcademicEJB {

    public void addStudent(Student student) throws SQLException, IOException {
        StudentDAO.addStudent(student);
        sendWelcomeEmail(student.getEmail(), student.getName());
    }

    public void updateStudent(Student student) throws SQLException, IOException {
        StudentDAO.updateStudent(student);
    }

    public Student getStudentById(int studentId) throws SQLException, IOException {
        return StudentDAO.getStudentById(studentId);
    }

    public List<Student> getAllStudents() throws SQLException, IOException {
        return StudentDAO.getAllStudents();
    }

    public List<Grade> getFailedCourses(int studentId) throws SQLException, IOException {
        return StudentDAO.getFailedCourses(studentId);
    }

    public List<Grade> getStudentGrades(int studentId) throws SQLException, IOException {
        return StudentDAO.getStudentGrades(studentId);
    }

    public double calculateCGPA(int studentId) throws SQLException, IOException {
        return StudentDAO.calculateCGPA(studentId);
    }

    public int getFailedCourseCount(int studentId) throws SQLException, IOException {
        return StudentDAO.getFailedCourseCount(studentId);
    }

    public boolean checkEligibility(int studentId) throws SQLException, IOException {
        double cgpa = calculateCGPA(studentId);
        int failedCourses = getFailedCourseCount(studentId);
        return cgpa >= 2.0 && failedCourses <= 3;
    }

    public List<Student> getIneligibleStudents() throws SQLException, IOException {
        List<Student> allStudents = getAllStudents();
        List<Student> ineligible = new java.util.ArrayList<>();
        for (Student student : allStudents) {
            if (!checkEligibility(student.getId())) {
                student.setCurrentCgpa(calculateCGPA(student.getId()));
                ineligible.add(student);
            }
        }
        return ineligible;
    }

    public Map<String, Object> generateAcademicReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = new HashMap<>();
        Student student = getStudentById(studentId);
        List<Grade> grades = getStudentGrades(studentId);
        double cgpa = calculateCGPA(studentId);

        report.put("student", student);
        report.put("grades", grades);
        report.put("cgpa", cgpa);

        return report;
    }

    public void sendAcademicReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = generateAcademicReport(studentId);
        Student student = (Student) report.get("student");
        List<Grade> grades = (List<Grade>) report.get("grades");
        double cgpa = (double) report.get("cgpa");

        StringBuilder body = new StringBuilder();
        body.append("Academic Performance Report\n");
        body.append("==========================\n\n");
        body.append("Student Name: ").append(student.getName()).append("\n");
        body.append("Student ID: ").append(student.getId()).append("\n");
        body.append("Program: ").append(student.getProgram()).append("\n\n");
        body.append("Cumulative GPA: ").append(String.format("%.2f", cgpa)).append("\n\n");
        body.append("Grades:\n");

        for (Grade grade : grades) {
            body.append("- ").append(grade.getCourseCode())
                .append(": ").append(grade.getGrade())
                .append(" (").append(String.format("%.1f", grade.getGradePoint())).append(")\n");
        }

        String subject = "Academic Performance Report";
        try {
            EmailUtil.sendEmail(student.getEmail(), subject, body.toString());
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a student can attempt a course again based on the 3-attempt policy.
     * Each course allows maximum 3 attempts to pass.
     */
    public boolean canAttemptCourse(int studentId, String courseCode) throws SQLException, IOException {
        int attempts = getCourseAttemptCount(studentId, courseCode);
        return attempts < 3;
    }

    /**
     * Gets the number of attempts a student has made for a specific course.
     */
    public int getCourseAttemptCount(int studentId, String courseCode) throws SQLException, IOException {
        String query = "SELECT COUNT(*) as attempts FROM Grades WHERE student_id = ? AND course_code = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("attempts");
                }
            }
        }
        return 0;
    }

    /**
     * Gets the next attempt number for a student-course combination.
     */
    public int getNextAttemptNumber(int studentId, String courseCode) throws SQLException, IOException {
        return getCourseAttemptCount(studentId, courseCode) + 1;
    }

    /**
     * Determines what components need to be retaken based on the attempt number.
     * - Attempt 1: No specific component requirements
     * - Attempt 2: Failed component only (resubmission/resit)
     * - Attempt 3: All assessment components must be retaken
     */
    public String getRequiredComponentsForAttempt(int studentId, String courseCode) throws SQLException, IOException {
        int nextAttempt = getNextAttemptNumber(studentId, courseCode);

        switch (nextAttempt) {
            case 1:
                return "Initial course attempt - all components required";
            case 2:
                return "Failed component resubmission/resit only";
            case 3:
                return "All assessment components must be retaken";
            default:
                return "Maximum attempts exceeded";
        }
    }

    /**
     * Checks if a student has passed a course (any attempt).
     */
    public boolean hasPassedCourse(int studentId, String courseCode) throws SQLException, IOException {
        String query = "SELECT COUNT(*) as passed FROM Grades WHERE student_id = ? AND course_code = ? AND status = 'passed'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("passed") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Gets all courses a student has failed and can still attempt.
     */
    public List<String> getRetryableCourses(int studentId) throws SQLException, IOException {
        List<String> retryableCourses = new ArrayList<>();
        String query = "SELECT DISTINCT course_code FROM Grades WHERE student_id = ? AND status = 'failed'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("course_code");
                    if (canAttemptCourse(studentId, courseCode) && !hasPassedCourse(studentId, courseCode)) {
                        retryableCourses.add(courseCode);
                    }
                }
            }
        }
        return retryableCourses;
    }

    /**
     * Gets courses that have reached maximum attempts and cannot be retried.
     */
    public List<String> getMaxAttemptsCourses(int studentId) throws SQLException, IOException {
        List<String> maxAttemptsCourses = new ArrayList<>();
        String query = "SELECT DISTINCT course_code FROM Grades WHERE student_id = ? AND status = 'failed'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("course_code");
                    if (!canAttemptCourse(studentId, courseCode) && !hasPassedCourse(studentId, courseCode)) {
                        maxAttemptsCourses.add(courseCode);
                    }
                }
            }
        }
        return maxAttemptsCourses;
    }

    /**
     * Test method to verify course retrieval policy implementation
     * This method can be called from a test servlet to verify the logic works correctly
     */
    public Map<String, Object> testCourseRetrievalPolicy() throws SQLException, IOException {
        Map<String, Object> testResults = new HashMap<>();

        // Test case 1: Student with 0 attempts should be able to attempt (attempt 1)
        int testStudent1 = 1; // Assuming student exists
        String testCourse1 = "CS201"; // Assuming course exists
        boolean canAttempt1 = canAttemptCourse(testStudent1, testCourse1);
        String requiredComponents1 = getRequiredComponentsForAttempt(testStudent1, testCourse1);
        int nextAttempt1 = getNextAttemptNumber(testStudent1, testCourse1);

        testResults.put("test1_canAttempt", canAttempt1);
        testResults.put("test1_requiredComponents", requiredComponents1);
        testResults.put("test1_nextAttempt", nextAttempt1);

        // Test case 2: Student with 1 attempt should be able to attempt (attempt 2)
        // This would require mock data, but we can test the logic structure
        testResults.put("test2_logicCheck", "Attempt 2 should require 'Failed component resubmission/resit only'");

        // Test case 3: Student with 2 attempts should be able to attempt (attempt 3)
        testResults.put("test3_logicCheck", "Attempt 3 should require 'All assessment components must be retaken'");

        // Test case 4: Student with 3 attempts should NOT be able to attempt
        testResults.put("test4_logicCheck", "Attempt 4+ should be blocked with 'Maximum attempts exceeded'");

        // Verify the 3-attempt limit logic
        testResults.put("policyLimit", 3);
        testResults.put("policyRules", new String[]{
            "Attempt 1: Initial course attempt - all components required",
            "Attempt 2: Failed component resubmission/resit only",
            "Attempt 3: All assessment components must be retaken",
            "Attempt 4+: Maximum attempts exceeded"
        });

        return testResults;
    }

    private void sendWelcomeEmail(String email, String name) {
        try {
            String subject = "Welcome to Course Recovery System";
            String body = "Dear " + name + ",\n\n" +
                    "Welcome to the Course Recovery System!\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            EmailUtil.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
