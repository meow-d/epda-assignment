package com.crs.servlet;

import com.crs.dao.RecoveryDAO;
import com.crs.dao.StudentDAO;
import com.crs.model.Grade;
import com.crs.model.RecoveryPlan;
import com.crs.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/officer/*")
public class OfficerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
            return;
        }

        switch (path) {
            case "/recovery-plan":
                handleRecoveryPlan(request, response);
                break;
            case "/eligibility":
                handleEligibility(request, response);
                break;
            case "/academic-report":
                handleAcademicReport(request, response);
                break;
            case "/students":
                handleListStudents(request, response);
                break;
            default:
                request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/officer/dashboard.jsp");
            return;
        }

        switch (path) {
            case "/add-recovery-plan":
                handleAddRecoveryPlan(request, response);
                break;
            case "/update-recovery-plan":
                handleUpdateRecoveryPlan(request, response);
                break;
            case "/delete-recovery-plan":
                handleDeleteRecoveryPlan(request, response);
                break;
            case "/send-report":
                handleSendReport(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/officer/dashboard.jsp");
        }
    }

    private void handleRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentIdStr = request.getParameter("studentId");

        try {
            if (studentIdStr != null && !studentIdStr.isEmpty()) {
                int studentId = Integer.parseInt(studentIdStr);
                Student student = StudentDAO.getStudentById(studentId);
                List<Grade> failedCourses = StudentDAO.getFailedCourses(studentId);
                List<RecoveryPlan> recoveryPlans = RecoveryDAO.getRecoveryPlansByStudent(studentId);

                request.setAttribute("student", student);
                request.setAttribute("failedCourses", failedCourses);
                request.setAttribute("recoveryPlans", recoveryPlans);
            }

            List<Student> students = StudentDAO.getAllStudents();
            request.setAttribute("students", students);

            request.getRequestDispatcher("/officer/recoveryPlan.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load recovery plan: " + e.getMessage());
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        }
    }

    private void handleEligibility(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Student> allStudents = StudentDAO.getAllStudents();
            List<Map<String, Object>> eligibilityList = new java.util.ArrayList<>();

            for (Student student : allStudents) {
                Map<String, Object> eligibilityInfo = new HashMap<>();
                double cgpa = StudentDAO.calculateCGPA(student.getId());
                int failedCourses = StudentDAO.getFailedCourseCount(student.getId());
                boolean eligible = cgpa >= 2.0 && failedCourses <= 3;

                eligibilityInfo.put("student", student);
                eligibilityInfo.put("cgpa", cgpa);
                eligibilityInfo.put("failedCourses", failedCourses);
                eligibilityInfo.put("eligible", eligible);

                eligibilityList.add(eligibilityInfo);
            }

            request.setAttribute("eligibilityList", eligibilityList);
            request.getRequestDispatcher("/officer/eligibility.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load eligibility information: " + e.getMessage());
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        }
    }

    private void handleAcademicReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentIdStr = request.getParameter("studentId");

        try {
            if (studentIdStr != null && !studentIdStr.isEmpty()) {
                int studentId = Integer.parseInt(studentIdStr);
                Student student = StudentDAO.getStudentById(studentId);
                List<Grade> grades = StudentDAO.getStudentGrades(studentId);
                double cgpa = StudentDAO.calculateCGPA(studentId);

                request.setAttribute("student", student);
                request.setAttribute("grades", grades);
                request.setAttribute("cgpa", cgpa);
            }

            List<Student> students = StudentDAO.getAllStudents();
            request.setAttribute("students", students);

            request.getRequestDispatcher("/officer/academicReport.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load academic report: " + e.getMessage());
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        }
    }

    private void handleListStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Student> students = StudentDAO.getAllStudents();
            request.setAttribute("students", students);
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load students: " + e.getMessage());
            request.getRequestDispatcher("/officer/dashboard.jsp").forward(request, response);
        }
    }

    private void handleAddRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        String courseCode = request.getParameter("courseCode");
        String task = request.getParameter("task");
        String deadlineStr = request.getParameter("deadline");

        if (task == null || deadlineStr == null || task.isEmpty() || deadlineStr.isEmpty()) {
            request.setAttribute("error", "Task and deadline are required");
            handleRecoveryPlan(request, response);
            return;
        }

        try {
            Timestamp deadline = Timestamp.valueOf(deadlineStr);

            RecoveryPlan recoveryPlan = new RecoveryPlan();
            recoveryPlan.setStudentId(studentId);
            recoveryPlan.setCourseCode(courseCode);
            recoveryPlan.setTask(task);
            recoveryPlan.setDeadline(deadline);
            recoveryPlan.setStatus("active");

            RecoveryDAO.addRecoveryPlan(recoveryPlan);
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + studentId);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to add recovery plan: " + e.getMessage());
            handleRecoveryPlan(request, response);
        }
    }

    private void handleUpdateRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int recoveryPlanId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");

        try {
            RecoveryPlan recoveryPlan = RecoveryDAO.getRecoveryPlanById(recoveryPlanId);
            if (recoveryPlan != null) {
                recoveryPlan.setStatus(status);
                RecoveryDAO.updateRecoveryPlan(recoveryPlan);
            }
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + recoveryPlan.getStudentId());
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update recovery plan: " + e.getMessage());
            handleRecoveryPlan(request, response);
        }
    }

    private void handleDeleteRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int recoveryPlanId = Integer.parseInt(request.getParameter("id"));
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        try {
            RecoveryDAO.deleteRecoveryPlan(recoveryPlanId);
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + studentId);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to delete recovery plan: " + e.getMessage());
            handleRecoveryPlan(request, response);
        }
    }

    private void handleSendReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        try {
            com.crs.ejb.AcademicEJB academicEJB = new com.crs.ejb.AcademicEJB();
            academicEJB.sendAcademicReport(studentId);
            request.setAttribute("success", "Academic report sent successfully");
            handleAcademicReport(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to send report: " + e.getMessage());
            handleAcademicReport(request, response);
        }
    }
}
