package com.crs.dao;

import com.crs.model.ActionPlan;
import com.crs.util.DBConnect;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActionPlanDAO {
    private static final String INSERT_ACTION_PLAN = "INSERT INTO ActionPlans (milestone_id, student_id, course_code, task, deadline, status, grade, grade_point, progress_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ACTION_PLAN = "UPDATE ActionPlans SET milestone_id = ?, student_id = ?, course_code = ?, task = ?, deadline = ?, status = ?, grade = ?, grade_point = ?, progress_notes = ? WHERE id = ?";
    private static final String DELETE_ACTION_PLAN = "DELETE FROM ActionPlans WHERE id = ?";
    private static final String SELECT_ACTION_PLAN_BY_ID = "SELECT * FROM ActionPlans WHERE id = ?";
    private static final String SELECT_ACTION_PLANS_BY_STUDENT = "SELECT * FROM ActionPlans WHERE student_id = ? ORDER BY deadline ASC";
    private static final String SELECT_ACTION_PLANS_BY_MILESTONE = "SELECT * FROM ActionPlans WHERE milestone_id = ? ORDER BY deadline ASC";
    private static final String SELECT_ACTION_PLANS_BY_COURSE = "SELECT * FROM ActionPlans WHERE student_id = ? AND course_code = ? ORDER BY deadline ASC";
    private static final String SELECT_ALL_ACTION_PLANS = "SELECT ap.*, s.name as student_name, c.title as course_title FROM ActionPlans ap JOIN Students s ON ap.student_id = s.id JOIN Courses c ON ap.course_code = c.code";
    private static final String SELECT_ACTION_PLANS_BY_STATUS = "SELECT ap.*, s.name as student_name, c.title as course_title FROM ActionPlans ap JOIN Students s ON ap.student_id = s.id JOIN Courses c ON ap.course_code = c.code WHERE ap.status = ?";

    public static void addActionPlan(ActionPlan actionPlan) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ACTION_PLAN, Statement.RETURN_GENERATED_KEYS)) {
            if (actionPlan.getMilestoneId() != null) {
                ps.setInt(1, actionPlan.getMilestoneId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, actionPlan.getStudentId());
            ps.setString(3, actionPlan.getCourseCode());
            ps.setString(4, actionPlan.getTask());
            ps.setTimestamp(5, actionPlan.getDeadline());
            ps.setString(6, actionPlan.getStatus());
            ps.setString(7, actionPlan.getGrade());
            if (actionPlan.getGradePoint() != null) {
                ps.setDouble(8, actionPlan.getGradePoint());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }
            ps.setString(9, actionPlan.getProgressNotes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    actionPlan.setId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateActionPlan(ActionPlan actionPlan) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ACTION_PLAN)) {
            if (actionPlan.getMilestoneId() != null) {
                ps.setInt(1, actionPlan.getMilestoneId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, actionPlan.getStudentId());
            ps.setString(3, actionPlan.getCourseCode());
            ps.setString(4, actionPlan.getTask());
            ps.setTimestamp(5, actionPlan.getDeadline());
            ps.setString(6, actionPlan.getStatus());
            ps.setString(7, actionPlan.getGrade());
            if (actionPlan.getGradePoint() != null) {
                ps.setDouble(8, actionPlan.getGradePoint());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }
            ps.setString(9, actionPlan.getProgressNotes());
            ps.setInt(10, actionPlan.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteActionPlan(int actionPlanId) throws SQLException, IOException {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ACTION_PLAN)) {
            ps.setInt(1, actionPlanId);
            ps.executeUpdate();
        }
    }

    public static ActionPlan getActionPlanById(int actionPlanId) throws SQLException, IOException {
        ActionPlan actionPlan = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTION_PLAN_BY_ID)) {
            ps.setInt(1, actionPlanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actionPlan = mapResultSetToActionPlan(rs);
                }
            }
        }
        return actionPlan;
    }

    public static List<ActionPlan> getActionPlansByStudent(int studentId) throws SQLException, IOException {
        List<ActionPlan> actionPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTION_PLANS_BY_STUDENT)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actionPlans.add(mapResultSetToActionPlan(rs));
                }
            }
        }
        return actionPlans;
    }

    public static List<ActionPlan> getActionPlansByMilestone(int milestoneId) throws SQLException, IOException {
        List<ActionPlan> actionPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTION_PLANS_BY_MILESTONE)) {
            ps.setInt(1, milestoneId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actionPlans.add(mapResultSetToActionPlan(rs));
                }
            }
        }
        return actionPlans;
    }

    public static List<ActionPlan> getActionPlansByStudentAndCourse(int studentId, String courseCode) throws SQLException, IOException {
        List<ActionPlan> actionPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTION_PLANS_BY_COURSE)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actionPlans.add(mapResultSetToActionPlan(rs));
                }
            }
        }
        return actionPlans;
    }

    public static List<ActionPlan> getAllActionPlans() throws SQLException, IOException {
        List<ActionPlan> actionPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_ACTION_PLANS)) {
            while (rs.next()) {
                actionPlans.add(mapResultSetToActionPlan(rs));
            }
        }
        return actionPlans;
    }

    public static List<ActionPlan> getActionPlansByStatus(String status) throws SQLException, IOException {
        List<ActionPlan> actionPlans = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTION_PLANS_BY_STATUS)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actionPlans.add(mapResultSetToActionPlan(rs));
                }
            }
        }
        return actionPlans;
    }

    private static ActionPlan mapResultSetToActionPlan(ResultSet rs) throws SQLException {
        ActionPlan actionPlan = new ActionPlan();
        actionPlan.setId(rs.getInt("id"));

        int milestoneId = rs.getInt("milestone_id");
        if (!rs.wasNull()) {
            actionPlan.setMilestoneId(milestoneId);
        }

        actionPlan.setStudentId(rs.getInt("student_id"));
        actionPlan.setCourseCode(rs.getString("course_code"));
        actionPlan.setTask(rs.getString("task"));
        actionPlan.setDeadline(rs.getTimestamp("deadline"));
        actionPlan.setStatus(rs.getString("status"));
        actionPlan.setGrade(rs.getString("grade"));

        double gradePoint = rs.getDouble("grade_point");
        if (!rs.wasNull()) {
            actionPlan.setGradePoint(gradePoint);
        }

        actionPlan.setProgressNotes(rs.getString("progress_notes"));
        actionPlan.setCreatedAt(rs.getTimestamp("created_at"));
        actionPlan.setUpdatedAt(rs.getTimestamp("updated_at"));
        return actionPlan;
    }
}