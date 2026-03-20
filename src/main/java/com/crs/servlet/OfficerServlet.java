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
        System.out.println("========== [OfficerServlet] doGet START ==========");
        System.out.println("[OfficerServlet] Request URI: " + request.getRequestURI());
        System.out.println("[OfficerServlet] Path Info: " + request.getPathInfo());
        System.out.println("[OfficerServlet] Session role: " + (request.getSession(false) != null ? request.getSession().getAttribute("role") : "null"));
        
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
            (!"officer".equals(session.getAttribute("role")) && !"admin".equals(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();
        System.out.println("[OfficerServlet] doGet path: " + path);

        if (path == null || path.equals("/") || path.isEmpty()) {
            request.setAttribute("currentPage", "dashboard");
            request.getRequestDispatcher("/WEB-INF/officer/index.jsp").forward(request, response);
            return;
        }

        // Normalize path - remove trailing slashes
        path = path.replaceAll("/+$", "");
        
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
                System.out.println("[OfficerServlet] Unknown path, defaulting to dashboard: " + path);
                request.setAttribute("currentPage", "dashboard");
                request.getRequestDispatcher("/WEB-INF/officer/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
            (!"officer".equals(session.getAttribute("role")) && !"admin".equals(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();

        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/WEB-INF/officer/index.jsp");
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
                response.sendRedirect(request.getContextPath() + "/WEB-INF/officer/index.jsp");
        }
    }

    private void handleRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleRecoveryPlan called");
        request.setAttribute("currentPage", "recovery-plan");
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

            request.getRequestDispatcher("/WEB-INF/officer/recoveryPlan.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleRecoveryPlan ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleRecoveryPlan: " + e.getMessage(), e);
        }
    }

    private void handleEligibility(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleEligibility called");
        request.setAttribute("currentPage", "eligibility");
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
            request.getRequestDispatcher("/WEB-INF/officer/eligibility.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] ERROR in handleEligibility ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleEligibility: " + e.getMessage(), e);
        }
    }

    private void handleAcademicReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleAcademicReport called");
        request.setAttribute("currentPage", "academic-report");
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

            request.getRequestDispatcher("/WEB-INF/officer/academicReport.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleAcademicReport ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleAcademicReport: " + e.getMessage(), e);
        }
    }

    private void handleListStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleListStudents called");
        request.setAttribute("currentPage", "dashboard");
        try {
            List<Student> students = StudentDAO.getAllStudents();
            request.setAttribute("students", students);
            request.getRequestDispatcher("/WEB-INF/officer/index.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleListStudents ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleListStudents: " + e.getMessage(), e);
        }
    }

    private void handleAddRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleAddRecoveryPlan called");
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
            System.out.println("[OfficerServlet] Recovery plan added successfully, redirecting...");
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + studentId);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleAddRecoveryPlan ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleAddRecoveryPlan: " + e.getMessage(), e);
        }
    }

    private void handleUpdateRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleUpdateRecoveryPlan called");
        int recoveryPlanId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");

        try {
            RecoveryPlan recoveryPlan = RecoveryDAO.getRecoveryPlanById(recoveryPlanId);
            if (recoveryPlan != null) {
                recoveryPlan.setStatus(status);
                RecoveryDAO.updateRecoveryPlan(recoveryPlan);
            }
            System.out.println("[OfficerServlet] Recovery plan updated, redirecting...");
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + recoveryPlan.getStudentId());
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleUpdateRecoveryPlan ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleUpdateRecoveryPlan: " + e.getMessage(), e);
        }
    }

    private void handleDeleteRecoveryPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleDeleteRecoveryPlan called");
        int recoveryPlanId = Integer.parseInt(request.getParameter("id"));
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        try {
            RecoveryDAO.deleteRecoveryPlan(recoveryPlanId);
            System.out.println("[OfficerServlet] Recovery plan deleted, redirecting...");
            response.sendRedirect(request.getContextPath() + "/officer/recovery-plan?studentId=" + studentId);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] SQL ERROR in handleDeleteRecoveryPlan ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleDeleteRecoveryPlan: " + e.getMessage(), e);
        }
    }

    private void handleSendReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[OfficerServlet] handleSendReport called");
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        try {
            com.crs.ejb.AcademicEJB academicEJB = new com.crs.ejb.AcademicEJB();
            academicEJB.sendAcademicReport(studentId);
            request.setAttribute("success", "Academic report sent successfully");
            handleAcademicReport(request, response);
        } catch (Exception e) {
            System.err.println("========== [OfficerServlet] ERROR in handleSendReport ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleSendReport: " + e.getMessage(), e);
        }
    }
}
