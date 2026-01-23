package com.crs.dao;

import com.crs.model.Milestone;
import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MilestoneDAO {
    private static final String INSERT_MILESTONE = "INSERT INTO Milestones (student_id, course_code, title, description, target_date, status) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_MILESTONE = "UPDATE Milestones SET student_id = ?, course_code = ?, title = ?, description = ?, target_date = ?, status = ? WHERE id = ?";
    private static final String DELETE_MILESTONE = "DELETE FROM Milestones WHERE id = ?";
    private static final String SELECT_MILESTONE_BY_ID = "SELECT * FROM Milestones WHERE id = ?";
    private static final String SELECT_MILESTONES_BY_STUDENT = "SELECT * FROM Milestones WHERE student_id = ? ORDER BY target_date ASC";
    private static final String SELECT_MILESTONES_BY_COURSE = "SELECT * FROM Milestones WHERE student_id = ? AND course_code = ? ORDER BY target_date ASC";
    private static final String SELECT_ALL_MILESTONES = "SELECT m.*, s.name as student_name, c.title as course_title FROM Milestones m JOIN Students s ON m.student_id = s.id JOIN Courses c ON m.course_code = c.code";
    private static final String SELECT_MILESTONES_BY_STATUS = "SELECT m.*, s.name as student_name, c.title as course_title FROM Milestones m JOIN Students s ON m.student_id = s.id JOIN Courses c ON m.course_code = c.code WHERE m.status = ?";

    public static void addMilestone(Milestone milestone) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_MILESTONE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, milestone.getStudentId());
            ps.setString(2, milestone.getCourseCode());
            ps.setString(3, milestone.getTitle());
            ps.setString(4, milestone.getDescription());
            ps.setDate(5, milestone.getTargetDate());
            ps.setString(6, milestone.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    milestone.setId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateMilestone(Milestone milestone) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_MILESTONE)) {
            ps.setInt(1, milestone.getStudentId());
            ps.setString(2, milestone.getCourseCode());
            ps.setString(3, milestone.getTitle());
            ps.setString(4, milestone.getDescription());
            ps.setDate(5, milestone.getTargetDate());
            ps.setString(6, milestone.getStatus());
            ps.setInt(7, milestone.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteMilestone(int milestoneId) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_MILESTONE)) {
            ps.setInt(1, milestoneId);
            ps.executeUpdate();
        }
    }

    public static Milestone getMilestoneById(int milestoneId) throws SQLException, IOException {
        Milestone milestone = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_MILESTONE_BY_ID)) {
            ps.setInt(1, milestoneId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    milestone = mapResultSetToMilestone(rs);
                }
            }
        }
        return milestone;
    }

    public static List<Milestone> getMilestonesByStudent(int studentId) throws SQLException, IOException {
        List<Milestone> milestones = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_MILESTONES_BY_STUDENT)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    milestones.add(mapResultSetToMilestone(rs));
                }
            }
        }
        return milestones;
    }

    public static List<Milestone> getMilestonesByStudentAndCourse(int studentId, String courseCode) throws SQLException, IOException {
        List<Milestone> milestones = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_MILESTONES_BY_COURSE)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    milestones.add(mapResultSetToMilestone(rs));
                }
            }
        }
        return milestones;
    }

    public static List<Milestone> getAllMilestones() throws SQLException, IOException {
        List<Milestone> milestones = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_MILESTONES)) {
            while (rs.next()) {
                milestones.add(mapResultSetToMilestone(rs));
            }
        }
        return milestones;
    }

    public static List<Milestone> getMilestonesByStatus(String status) throws SQLException, IOException {
        List<Milestone> milestones = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_MILESTONES_BY_STATUS)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    milestones.add(mapResultSetToMilestone(rs));
                }
            }
        }
        return milestones;
    }

    private static Milestone mapResultSetToMilestone(ResultSet rs) throws SQLException {
        Milestone milestone = new Milestone();
        milestone.setId(rs.getInt("id"));
        milestone.setStudentId(rs.getInt("student_id"));
        milestone.setCourseCode(rs.getString("course_code"));
        milestone.setTitle(rs.getString("title"));
        milestone.setDescription(rs.getString("description"));
        milestone.setTargetDate(rs.getDate("target_date"));
        milestone.setStatus(rs.getString("status"));
        milestone.setCreatedAt(rs.getTimestamp("created_at"));
        return milestone;
    }
}