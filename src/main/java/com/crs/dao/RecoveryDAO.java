package com.crs.dao;

import com.crs.model.RecoveryPlan;
import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecoveryDAO {
    private static final String INSERT_RECOVERY_PLAN = "INSERT INTO RecoveryPlans (student_id, course_code, task, deadline, status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_RECOVERY_PLAN = "UPDATE RecoveryPlans SET student_id = ?, course_code = ?, task = ?, deadline = ?, status = ? WHERE id = ?";
    private static final String DELETE_RECOVERY_PLAN = "DELETE FROM RecoveryPlans WHERE id = ?";
    private static final String SELECT_RECOVERY_PLAN_BY_ID = "SELECT * FROM RecoveryPlans WHERE id = ?";
    private static final String SELECT_RECOVERY_PLANS_BY_STUDENT = "SELECT * FROM RecoveryPlans WHERE student_id = ?";
    private static final String SELECT_ALL_RECOVERY_PLANS = "SELECT * FROM RecoveryPlans";
    private static final String SELECT_RECOVERY_PLANS_BY_STATUS = "SELECT rp.*, s.name as student_name, c.title as course_title " +
            "FROM RecoveryPlans rp " +
            "JOIN Students s ON rp.student_id = s.id " +
            "JOIN Courses c ON rp.course_code = c.code " +
            "WHERE rp.status = ?";

    public static void addRecoveryPlan(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_RECOVERY_PLAN, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, recoveryPlan.getStudentId());
            ps.setString(2, recoveryPlan.getCourseCode());
            ps.setString(3, recoveryPlan.getTask());
            ps.setTimestamp(4, recoveryPlan.getDeadline());
            ps.setString(5, recoveryPlan.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    recoveryPlan.setId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateRecoveryPlan(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RECOVERY_PLAN)) {
            ps.setInt(1, recoveryPlan.getStudentId());
            ps.setString(2, recoveryPlan.getCourseCode());
            ps.setString(3, recoveryPlan.getTask());
            ps.setTimestamp(4, recoveryPlan.getDeadline());
            ps.setString(5, recoveryPlan.getStatus());
            ps.setInt(6, recoveryPlan.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteRecoveryPlan(int recoveryPlanId) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_RECOVERY_PLAN)) {
            ps.setInt(1, recoveryPlanId);
            ps.executeUpdate();
        }
    }

    public static RecoveryPlan getRecoveryPlanById(int recoveryPlanId) throws SQLException, IOException {
        RecoveryPlan recoveryPlan = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_RECOVERY_PLAN_BY_ID)) {
            ps.setInt(1, recoveryPlanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recoveryPlan = mapResultSetToRecoveryPlan(rs);
                }
            }
        }
        return recoveryPlan;
    }

    public static List<RecoveryPlan> getRecoveryPlansByStudent(int studentId) throws SQLException, IOException {
        List<RecoveryPlan> recoveryPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_RECOVERY_PLANS_BY_STUDENT)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recoveryPlans.add(mapResultSetToRecoveryPlan(rs));
                }
            }
        }
        return recoveryPlans;
    }

    public static List<RecoveryPlan> getAllRecoveryPlans() throws SQLException, IOException {
        List<RecoveryPlan> recoveryPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_RECOVERY_PLANS)) {
            while (rs.next()) {
                recoveryPlans.add(mapResultSetToRecoveryPlan(rs));
            }
        }
        return recoveryPlans;
    }

    public static List<RecoveryPlan> getRecoveryPlansByStatus(String status) throws SQLException, IOException {
        List<RecoveryPlan> recoveryPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_RECOVERY_PLANS_BY_STATUS)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recoveryPlans.add(mapResultSetToRecoveryPlan(rs));
                }
            }
        }
        return recoveryPlans;
    }

    private static RecoveryPlan mapResultSetToRecoveryPlan(ResultSet rs) throws SQLException {
        RecoveryPlan recoveryPlan = new RecoveryPlan();
        recoveryPlan.setId(rs.getInt("id"));
        recoveryPlan.setStudentId(rs.getInt("student_id"));
        recoveryPlan.setCourseCode(rs.getString("course_code"));
        recoveryPlan.setTask(rs.getString("task"));
        recoveryPlan.setDeadline(rs.getTimestamp("deadline"));
        recoveryPlan.setStatus(rs.getString("status"));
        return recoveryPlan;
    }
}
