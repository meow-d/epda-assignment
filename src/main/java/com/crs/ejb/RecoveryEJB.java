package com.crs.ejb;

import com.crs.dao.RecoveryDAO;
import com.crs.dao.StudentDAO;
import com.crs.model.Grade;
import com.crs.model.RecoveryPlan;
import com.crs.model.Student;
import com.crs.util.EmailUtil;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
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
}
