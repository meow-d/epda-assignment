package com.crs.dao;

import com.crs.util.DBConnect;
import com.crs.model.User;
import com.crs.model.Student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsDAO {

    /**
     * Gets comprehensive system analytics
     */
    public static Map<String, Object> getSystemAnalytics() throws SQLException, IOException {
        Map<String, Object> analytics = new HashMap<>();

        // Basic counts
        analytics.put("totalUsers", getTotalUsers());
        analytics.put("totalStudents", getTotalStudents());
        analytics.put("activeRecoveryPlans", getActiveRecoveryPlansCount());
        analytics.put("completedRecoveryPlans", getCompletedRecoveryPlansCount());

        // Eligibility statistics
        Map<String, Integer> eligibilityStats = getEligibilityStatistics();
        analytics.put("eligibleStudents", eligibilityStats.get("eligible"));
        analytics.put("ineligibleStudents", eligibilityStats.get("ineligible"));

        // Course failure statistics
        analytics.put("totalFailedCourses", getTotalFailedCourses());
        analytics.put("coursesWithMultipleFailures", getCoursesWithMultipleFailures());

        // Recovery success rates
        analytics.put("recoverySuccessRate", calculateRecoverySuccessRate());

        // Recent activity
        analytics.put("recentRecoveryPlans", getRecentRecoveryPlans(5));

        return analytics;
    }

    /**
     * Gets academic performance analytics by semester
     */
    public static Map<String, Object> getAcademicAnalytics() throws SQLException, IOException {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("cgpaDistribution", getCGPADistribution());
        analytics.put("gradeDistribution", getGradeDistribution());
        analytics.put("failedCoursesBySemester", getFailedCoursesBySemester());

        return analytics;
    }

    private static int getTotalUsers() throws SQLException, IOException {
        String query = "SELECT COUNT(*) as count FROM Users";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private static int getTotalStudents() throws SQLException, IOException {
        String query = "SELECT COUNT(*) as count FROM Students";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private static int getActiveRecoveryPlansCount() throws SQLException, IOException {
        String query = "SELECT COUNT(*) as count FROM RecoveryPlans WHERE status = 'active'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private static int getCompletedRecoveryPlansCount() throws SQLException, IOException {
        String query = "SELECT COUNT(*) as count FROM RecoveryPlans WHERE status = 'completed'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private static Map<String, Integer> getEligibilityStatistics() throws SQLException, IOException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT " +
            "SUM(CASE WHEN current_cgpa >= 2.0 AND (SELECT COUNT(DISTINCT course_code) FROM Grades WHERE student_id = s.id AND status = 'failed') <= 3 THEN 1 ELSE 0 END) as eligible, " +
            "SUM(CASE WHEN current_cgpa < 2.0 OR (SELECT COUNT(DISTINCT course_code) FROM Grades WHERE student_id = s.id AND status = 'failed') > 3 THEN 1 ELSE 0 END) as ineligible " +
            "FROM Students s";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("eligible", rs.getInt("eligible"));
                stats.put("ineligible", rs.getInt("ineligible"));
            }
        }
        return stats;
    }

    private static int getTotalFailedCourses() throws SQLException, IOException {
        String query = "SELECT COUNT(*) as count FROM Grades WHERE status = 'failed'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private static Map<String, Integer> getCoursesWithMultipleFailures() throws SQLException, IOException {
        Map<String, Integer> courses = new HashMap<>();
        String query = "SELECT course_code, COUNT(*) as failure_count " +
            "FROM Grades WHERE status = 'failed' " +
            "GROUP BY course_code HAVING COUNT(*) > 1 " +
            "ORDER BY failure_count DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.put(rs.getString("course_code"), rs.getInt("failure_count"));
            }
        }
        return courses;
    }

    private static double calculateRecoverySuccessRate() throws SQLException, IOException {
        String query = "SELECT " +
            "(SELECT COUNT(*) FROM RecoveryPlans WHERE status = 'completed') * 100.0 / " +
            "NULLIF((SELECT COUNT(*) FROM RecoveryPlans), 0) as success_rate";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble("success_rate") : 0.0;
        }
    }

    private static Map<String, Integer> getCGPADistribution() throws SQLException, IOException {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT " +
            "SUM(CASE WHEN current_cgpa >= 3.5 THEN 1 ELSE 0 END) as excellent, " +
            "SUM(CASE WHEN current_cgpa >= 3.0 AND current_cgpa < 3.5 THEN 1 ELSE 0 END) as good, " +
            "SUM(CASE WHEN current_cgpa >= 2.5 AND current_cgpa < 3.0 THEN 1 ELSE 0 END) as satisfactory, " +
            "SUM(CASE WHEN current_cgpa >= 2.0 AND current_cgpa < 2.5 THEN 1 ELSE 0 END) as pass, " +
            "SUM(CASE WHEN current_cgpa < 2.0 THEN 1 ELSE 0 END) as fail " +
            "FROM Students";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                distribution.put("Excellent (3.5+)", rs.getInt("excellent"));
                distribution.put("Good (3.0-3.49)", rs.getInt("good"));
                distribution.put("Satisfactory (2.5-2.99)", rs.getInt("satisfactory"));
                distribution.put("Pass (2.0-2.49)", rs.getInt("pass"));
                distribution.put("Fail (<2.0)", rs.getInt("fail"));
            }
        }
        return distribution;
    }

    private static Map<String, Integer> getGradeDistribution() throws SQLException, IOException {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT grade, COUNT(*) as count FROM Grades GROUP BY grade ORDER BY grade";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString("grade"), rs.getInt("count"));
            }
        }
        return distribution;
    }

    private static Map<String, Integer> getFailedCoursesBySemester() throws SQLException, IOException {
        Map<String, Integer> semesterStats = new HashMap<>();
        String query = "SELECT CONCAT(semester, ' ', year) as semester_year, COUNT(*) as count " +
            "FROM Grades WHERE status = 'failed' " +
            "GROUP BY semester, year ORDER BY year DESC, semester DESC LIMIT 10";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                semesterStats.put(rs.getString("semester_year"), rs.getInt("count"));
            }
        }
        return semesterStats;
    }

    private static Map<String, Object> getRecentRecoveryPlans(int limit) throws SQLException, IOException {
        Map<String, Object> recentPlans = new HashMap<>();
        String query = "SELECT rp.*, s.name as student_name, c.title as course_title " +
            "FROM RecoveryPlans rp " +
            "JOIN Students s ON rp.student_id = s.id " +
            "JOIN Courses c ON rp.course_code = c.code " +
            "ORDER BY rp.created_at DESC LIMIT ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> plan = new HashMap<>();
                    plan.put("studentName", rs.getString("student_name"));
                    plan.put("courseTitle", rs.getString("course_title"));
                    plan.put("task", rs.getString("task"));
                    plan.put("status", rs.getString("status"));
                    plan.put("deadline", rs.getTimestamp("deadline"));
                    recentPlans.put(String.valueOf(rs.getInt("id")), plan);
                }
            }
        }
        return recentPlans;
    }
}