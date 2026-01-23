package com.crs.ejb;

import com.crs.dao.*;
import com.crs.model.*;
import com.crs.util.EmailUtil;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RecoveryEJB {

    public void addRecoveryPlan(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        RecoveryDAO.addRecoveryPlan(recoveryPlan);
        sendRecoveryPlanEmail(recoveryPlan);
    }

    public void updateRecoveryPlan(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        RecoveryDAO.updateRecoveryPlan(recoveryPlan);
    }

    public void deleteRecoveryPlan(int recoveryPlanId) throws SQLException, IOException {
        RecoveryDAO.deleteRecoveryPlan(recoveryPlanId);
    }

    public RecoveryPlan getRecoveryPlanById(int recoveryPlanId) throws SQLException, IOException {
        return RecoveryDAO.getRecoveryPlanById(recoveryPlanId);
    }

    public List<RecoveryPlan> getRecoveryPlansByStudent(int studentId) throws SQLException, IOException {
        return RecoveryDAO.getRecoveryPlansByStudent(studentId);
    }

    public List<RecoveryPlan> getAllRecoveryPlans() throws SQLException, IOException {
        return RecoveryDAO.getAllRecoveryPlans();
    }

    public List<RecoveryPlan> getActiveRecoveryPlans() throws SQLException, IOException {
        return RecoveryDAO.getRecoveryPlansByStatus("active");
    }

    public List<RecoveryPlan> getCompletedRecoveryPlans() throws SQLException, IOException {
        return RecoveryDAO.getRecoveryPlansByStatus("completed");
    }

    public Map<String, Object> generateRecoveryReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = new HashMap<>();
        Student student = StudentDAO.getStudentById(studentId);
        List<Grade> failedCourses = StudentDAO.getFailedCourses(studentId);
        List<RecoveryPlan> recoveryPlans = RecoveryDAO.getRecoveryPlansByStudent(studentId);

        report.put("student", student);
        report.put("failedCourses", failedCourses);
        report.put("recoveryPlans", recoveryPlans);

        return report;
    }

    public void createRecoveryPlanForFailedCourse(int studentId, String courseCode, String task, Timestamp deadline) throws SQLException, IOException {
        RecoveryPlan recoveryPlan = new RecoveryPlan();
        recoveryPlan.setStudentId(studentId);
        recoveryPlan.setCourseCode(courseCode);
        recoveryPlan.setTask(task);
        recoveryPlan.setDeadline(deadline);
        recoveryPlan.setStatus("active");

        RecoveryDAO.addRecoveryPlan(recoveryPlan);
        sendRecoveryPlanEmail(recoveryPlan);
    }

    public void updateRecoveryPlanStatus(int recoveryPlanId, String status) throws SQLException, IOException {
        RecoveryPlan recoveryPlan = RecoveryDAO.getRecoveryPlanById(recoveryPlanId);
        if (recoveryPlan != null) {
            recoveryPlan.setStatus(status);
            RecoveryDAO.updateRecoveryPlan(recoveryPlan);
            sendStatusUpdateEmail(recoveryPlan);
        }
    }

    private void sendRecoveryPlanEmail(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(recoveryPlan.getStudentId());
        if (student != null) {
            String subject = "Course Recovery Plan Created";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "A recovery plan has been created for you:\n" +
                    "Course: " + recoveryPlan.getCourseCode() + "\n" +
                    "Task: " + recoveryPlan.getTask() + "\n" +
                    "Deadline: " + recoveryPlan.getDeadline() + "\n\n" +
                    "Please complete the task by the deadline.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStatusUpdateEmail(RecoveryPlan recoveryPlan) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(recoveryPlan.getStudentId());
        if (student != null) {
            String subject = "Recovery Plan Status Updated";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "Your recovery plan status has been updated:\n" +
                    "Course: " + recoveryPlan.getCourseCode() + "\n" +
                    "New Status: " + recoveryPlan.getStatus() + "\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== MILESTONE MANAGEMENT =====

    public void createMilestone(Milestone milestone) throws SQLException, IOException {
        MilestoneDAO.addMilestone(milestone);
        sendMilestoneEmail(milestone);
    }

    public void updateMilestone(Milestone milestone) throws SQLException, IOException {
        MilestoneDAO.updateMilestone(milestone);
    }

    public void deleteMilestone(int milestoneId) throws SQLException, IOException {
        MilestoneDAO.deleteMilestone(milestoneId);
    }

    public Milestone getMilestoneById(int milestoneId) throws SQLException, IOException {
        return MilestoneDAO.getMilestoneById(milestoneId);
    }

    public List<Milestone> getMilestonesByStudent(int studentId) throws SQLException, IOException {
        return MilestoneDAO.getMilestonesByStudent(studentId);
    }

    public List<Milestone> getMilestonesByStudentAndCourse(int studentId, String courseCode) throws SQLException, IOException {
        return MilestoneDAO.getMilestonesByStudentAndCourse(studentId, courseCode);
    }

    public List<Milestone> getAllMilestones() throws SQLException, IOException {
        return MilestoneDAO.getAllMilestones();
    }

    public List<Milestone> getActiveMilestones() throws SQLException, IOException {
        return MilestoneDAO.getMilestonesByStatus("active");
    }

    // ===== ACTION PLAN MANAGEMENT =====

    public void createActionPlan(ActionPlan actionPlan) throws SQLException, IOException {
        ActionPlanDAO.addActionPlan(actionPlan);
        sendActionPlanEmail(actionPlan);
    }

    public void updateActionPlan(ActionPlan actionPlan) throws SQLException, IOException {
        ActionPlanDAO.updateActionPlan(actionPlan);
    }

    public void deleteActionPlan(int actionPlanId) throws SQLException, IOException {
        ActionPlanDAO.deleteActionPlan(actionPlanId);
    }

    public ActionPlan getActionPlanById(int actionPlanId) throws SQLException, IOException {
        return ActionPlanDAO.getActionPlanById(actionPlanId);
    }

    public List<ActionPlan> getActionPlansByStudent(int studentId) throws SQLException, IOException {
        return ActionPlanDAO.getActionPlansByStudent(studentId);
    }

    public List<ActionPlan> getActionPlansByMilestone(int milestoneId) throws SQLException, IOException {
        return ActionPlanDAO.getActionPlansByMilestone(milestoneId);
    }

    public List<ActionPlan> getActionPlansByStudentAndCourse(int studentId, String courseCode) throws SQLException, IOException {
        return ActionPlanDAO.getActionPlansByStudentAndCourse(studentId, courseCode);
    }

    public List<ActionPlan> getAllActionPlans() throws SQLException, IOException {
        return ActionPlanDAO.getAllActionPlans();
    }

    public List<ActionPlan> getPendingActionPlans() throws SQLException, IOException {
        return ActionPlanDAO.getActionPlansByStatus("pending");
    }

    public List<ActionPlan> getCompletedActionPlans() throws SQLException, IOException {
        return ActionPlanDAO.getActionPlansByStatus("completed");
    }

    // ===== PROGRESS TRACKING =====

    public void updateActionPlanProgress(int actionPlanId, String status, String grade, Double gradePoint, String progressNotes) throws SQLException, IOException {
        ActionPlan actionPlan = ActionPlanDAO.getActionPlanById(actionPlanId);
        if (actionPlan != null) {
            actionPlan.setStatus(status);
            actionPlan.setGrade(grade);
            actionPlan.setGradePoint(gradePoint);
            actionPlan.setProgressNotes(progressNotes);
            ActionPlanDAO.updateActionPlan(actionPlan);
            sendProgressUpdateEmail(actionPlan);
        }
    }

    public void updateMilestoneStatus(int milestoneId, String status) throws SQLException, IOException {
        Milestone milestone = MilestoneDAO.getMilestoneById(milestoneId);
        if (milestone != null) {
            milestone.setStatus(status);
            MilestoneDAO.updateMilestone(milestone);
            sendMilestoneStatusEmail(milestone);
        }
    }

    // ===== COMPREHENSIVE RECOVERY MANAGEMENT =====

    public void createRecoveryPlanWithMilestones(int studentId, String courseCode, List<Milestone> milestones) throws SQLException, IOException {
        // Create milestones
        for (Milestone milestone : milestones) {
            milestone.setStudentId(studentId);
            milestone.setCourseCode(courseCode);
            createMilestone(milestone);
        }
    }

    public Map<String, Object> generateComprehensiveRecoveryReport(int studentId) throws SQLException, IOException {
        Map<String, Object> report = new HashMap<>();
        Student student = StudentDAO.getStudentById(studentId);
        List<Grade> failedCourses = StudentDAO.getFailedCourses(studentId);
        List<Milestone> milestones = MilestoneDAO.getMilestonesByStudent(studentId);
        List<ActionPlan> actionPlans = ActionPlanDAO.getActionPlansByStudent(studentId);

        report.put("student", student);
        report.put("failedCourses", failedCourses);
        report.put("milestones", milestones);
        report.put("actionPlans", actionPlans);

        return report;
    }

    // ===== EMAIL NOTIFICATIONS =====

    private void sendMilestoneEmail(Milestone milestone) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(milestone.getStudentId());
        if (student != null) {
            String subject = "New Milestone Created";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "A new milestone has been created for your course recovery:\n" +
                    "Course: " + milestone.getCourseCode() + "\n" +
                    "Milestone: " + milestone.getTitle() + "\n" +
                    "Target Date: " + milestone.getTargetDate() + "\n" +
                    "Description: " + milestone.getDescription() + "\n\n" +
                    "Please work towards completing this milestone.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendActionPlanEmail(ActionPlan actionPlan) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(actionPlan.getStudentId());
        if (student != null) {
            String subject = "New Action Plan Created";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "A new action plan has been created for you:\n" +
                    "Course: " + actionPlan.getCourseCode() + "\n" +
                    "Task: " + actionPlan.getTask() + "\n" +
                    "Deadline: " + actionPlan.getDeadline() + "\n\n" +
                    "Please complete this task by the deadline.\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendProgressUpdateEmail(ActionPlan actionPlan) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(actionPlan.getStudentId());
        if (student != null) {
            String subject = "Action Plan Progress Updated";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "Your action plan progress has been updated:\n" +
                    "Course: " + actionPlan.getCourseCode() + "\n" +
                    "Task: " + actionPlan.getTask() + "\n" +
                    "Status: " + actionPlan.getStatus() + "\n" +
                    (actionPlan.getGrade() != null ? "Grade: " + actionPlan.getGrade() + "\n" : "") +
                    "Progress Notes: " + actionPlan.getProgressNotes() + "\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMilestoneStatusEmail(Milestone milestone) throws SQLException, IOException {
        Student student = StudentDAO.getStudentById(milestone.getStudentId());
        if (student != null) {
            String subject = "Milestone Status Updated";
            String body = "Dear " + student.getName() + ",\n\n" +
                    "Your milestone status has been updated:\n" +
                    "Course: " + milestone.getCourseCode() + "\n" +
                    "Milestone: " + milestone.getTitle() + "\n" +
                    "New Status: " + milestone.getStatus() + "\n\n" +
                    "Best regards,\n" +
                    "Course Recovery System Team";
            try {
                EmailUtil.sendEmail(student.getEmail(), subject, body);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
