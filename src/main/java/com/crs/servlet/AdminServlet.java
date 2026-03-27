package com.crs.servlet;

import com.crs.dao.UserDAO;
import com.crs.dao.StudentDAO;
import com.crs.ejb.AnalyticsEJB;
import com.crs.ejb.UserEJB;
import com.crs.model.Grade;
import com.crs.model.Student;
import com.crs.model.User;
import com.crs.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    // Direct instantiation to avoid CDI issues
    private UserEJB userEJB = new UserEJB();
    private AnalyticsEJB analyticsEJB = new AnalyticsEJB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== [AdminServlet] doGet START ==========");
        System.out.println("[AdminServlet] Request URI: " + request.getRequestURI());
        System.out.println("[AdminServlet] Path Info: " + request.getPathInfo());
        System.out.println("[AdminServlet] Session role: " + (request.getSession(false) != null ? request.getSession().getAttribute("role") : "null"));
        
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            System.out.println("[AdminServlet] No session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role) && !"officer".equals(role)) {
            System.out.println("[AdminServlet] Access denied - role: " + role + ", redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            System.out.println("[AdminServlet] Showing dashboard");
            showDashboard(request, response);
            return;
        }

        switch (path) {
            case "/users":
                System.out.println("[AdminServlet] Handling /users");
                handleListUsers(request, response);
                break;
            case "/add-user":
                System.out.println("[AdminServlet] Handling /add-user");
                request.setAttribute("currentPage", "add-user");
                request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
                break;
            case "/edit-user":
                System.out.println("[AdminServlet] Handling /edit-user");
                handleEditUserForm(request, response);
                break;
            case "/eligibility":
                System.out.println("[AdminServlet] Handling /eligibility");
                handleEligibility(request, response);
                break;
            case "/academic-report":
                System.out.println("[AdminServlet] Handling /academic-report");
                handleAcademicReport(request, response);
                break;
            default:
                System.out.println("[AdminServlet] Unknown path: " + path + ", showing dashboard");
                showDashboard(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== [AdminServlet] doPost START ==========");
        System.out.println("[AdminServlet] Request URI: " + request.getRequestURI());
        System.out.println("[AdminServlet] Path Info: " + request.getPathInfo());
        
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            System.out.println("[AdminServlet] No session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role) && !"officer".equals(role)) {
            System.out.println("[AdminServlet] Access denied - role: " + role + ", redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();

        if (path == null) {
            System.out.println("[AdminServlet] Path is null, redirecting to /admin/");
            response.sendRedirect(request.getContextPath() + "/admin/");
            return;
        }

        switch (path) {
            case "/add-user":
                System.out.println("[AdminServlet] Handling POST /add-user");
                handleAddUser(request, response);
                break;
            case "/update-user":
                System.out.println("[AdminServlet] Handling POST /update-user");
                handleUpdateUser(request, response);
                break;
            case "/delete-user":
                System.out.println("[AdminServlet] Handling POST /delete-user");
                handleDeleteUser(request, response);
                break;
            case "/send-report":
                System.out.println("[AdminServlet] Handling POST /send-report");
                handleSendReport(request, response);
                break;
            default:
                System.out.println("[AdminServlet] Unknown POST path: " + path + ", redirecting to /admin/");
                response.sendRedirect(request.getContextPath() + "/admin/");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] showDashboard called");
        request.setAttribute("currentPage", "dashboard");

        // Get analytics data
        try {
            var analytics = analyticsEJB.getSystemAnalytics();
            System.out.println("[AdminServlet] Analytics loaded successfully");
            request.setAttribute("analytics", analytics);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR loading analytics ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Unable to load analytics: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleListUsers called");
        try {
            List<User> users = UserDAO.getAllUsers();
            System.out.println("[AdminServlet] Loaded " + users.size() + " users");
            request.setAttribute("users", users);
            request.setAttribute("currentPage", "users");
            request.getRequestDispatcher("/WEB-INF/admin/manageUsers.jsp").forward(request, response);
        } catch (SQLException e) {
            System.err.println("========== [AdminServlet] SQL ERROR in handleListUsers ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Database error in handleListUsers: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleListUsers ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Failed to load users: " + e.getMessage());
            request.setAttribute("currentPage", "users");
            request.getRequestDispatcher("/WEB-INF/admin/manageUsers.jsp").forward(request, response);
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleAddUser called");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        // Validate all inputs
        ValidationUtil.ValidationResult usernameValidation = ValidationUtil.validateUsername(username);
        ValidationUtil.ValidationResult passwordValidation = ValidationUtil.validatePassword(password);
        ValidationUtil.ValidationResult emailValidation = ValidationUtil.validateEmail(email);
        ValidationUtil.ValidationResult roleValidation = ValidationUtil.validateRole(role);
        ValidationUtil.ValidationResult statusValidation = status != null ? ValidationUtil.validateStatus(status) :
                                                         new ValidationUtil.ValidationResult(true, null);

        // Check if any validation failed
        if (!usernameValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + usernameValidation.getErrorMessage());
            request.setAttribute("error", usernameValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!passwordValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + passwordValidation.getErrorMessage());
            request.setAttribute("error", passwordValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!emailValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + emailValidation.getErrorMessage());
            request.setAttribute("error", emailValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!roleValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + roleValidation.getErrorMessage());
            request.setAttribute("error", roleValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!statusValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + statusValidation.getErrorMessage());
            request.setAttribute("error", statusValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(password);
            user.setRole(role.trim().toLowerCase());
            user.setEmail(email.trim().toLowerCase());
            user.setStatus(status != null ? status.trim().toLowerCase() : "active");

            userEJB.addUser(user);
            System.out.println("[AdminServlet] User added successfully, redirecting to /admin/users");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            System.err.println("========== [AdminServlet] SQL ERROR in handleAddUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Database error in handleAddUser: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleAddUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Failed to add user: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleUpdateUser called");
        String userIdStr = request.getParameter("id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        // Validate user ID
        ValidationUtil.ValidationResult userIdValidation = ValidationUtil.validateRequired(userIdStr, "User ID");
        if (!userIdValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + userIdValidation.getErrorMessage());
            request.setAttribute("error", userIdValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
        } catch (NumberFormatException e) {
            System.err.println("[AdminServlet] Invalid user ID format: " + userIdStr);
            request.setAttribute("error", "Invalid user ID");
            handleListUsers(request, response);
            return;
        }

        // Validate other inputs
        ValidationUtil.ValidationResult usernameValidation = ValidationUtil.validateUsername(username);
        ValidationUtil.ValidationResult emailValidation = ValidationUtil.validateEmail(email);
        ValidationUtil.ValidationResult roleValidation = ValidationUtil.validateRole(role);
        ValidationUtil.ValidationResult statusValidation = ValidationUtil.validateStatus(status);

        // Password is optional for updates
        ValidationUtil.ValidationResult passwordValidation = new ValidationUtil.ValidationResult(true, null);
        if (password != null && !password.trim().isEmpty()) {
            passwordValidation = ValidationUtil.validatePassword(password);
        }

        // Check if any validation failed
        if (!usernameValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + usernameValidation.getErrorMessage());
            request.setAttribute("error", usernameValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!emailValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + emailValidation.getErrorMessage());
            request.setAttribute("error", emailValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!roleValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + roleValidation.getErrorMessage());
            request.setAttribute("error", roleValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!statusValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + statusValidation.getErrorMessage());
            request.setAttribute("error", statusValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!passwordValidation.isValid()) {
            System.out.println("[AdminServlet] Validation failed: " + passwordValidation.getErrorMessage());
            request.setAttribute("error", passwordValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }

        try {
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                user.setUsername(username.trim());
                if (password != null && !password.trim().isEmpty()) {
                    user.setPassword(password);
                }
                user.setRole(role.trim().toLowerCase());
                user.setEmail(email.trim().toLowerCase());
                user.setStatus(status.trim().toLowerCase());

                UserDAO.updateUser(user);
                System.out.println("[AdminServlet] User updated successfully, redirecting to /admin/users");
            }
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            System.err.println("========== [AdminServlet] SQL ERROR in handleUpdateUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Database error in handleUpdateUser: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleUpdateUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Failed to update user: " + e.getMessage());
            handleListUsers(request, response);
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleDeleteUser called for userId: " + request.getParameter("id"));
        int userId = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO.deleteUser(userId);
            System.out.println("[AdminServlet] User deleted successfully, redirecting to /admin/users");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            System.err.println("========== [AdminServlet] SQL ERROR in handleDeleteUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Database error in handleDeleteUser: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleDeleteUser ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Failed to delete user: " + e.getMessage());
            handleListUsers(request, response);
        }
    }

    private void handleEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleEditUserForm called for userId: " + request.getParameter("id"));
        int userId = Integer.parseInt(request.getParameter("id"));

        try {
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                System.out.println("[AdminServlet] User found: " + user.getUsername());
                request.setAttribute("user", user);
                request.setAttribute("currentPage", "users");
                request.getRequestDispatcher("/WEB-INF/admin/editUser.jsp").forward(request, response);
            } else {
                System.out.println("[AdminServlet] User not found: " + userId);
                request.setAttribute("error", "User not found");
                request.setAttribute("currentPage", "users");
                handleListUsers(request, response);
            }
        } catch (SQLException e) {
            System.err.println("========== [AdminServlet] SQL ERROR in handleEditUserForm ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Database error in handleEditUserForm: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleEditUserForm ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            request.setAttribute("error", "Failed to load user: " + e.getMessage());
            request.setAttribute("currentPage", "users");
            handleListUsers(request, response);
        }
    }

    private void handleEligibility(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleEligibility called");
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
            request.getRequestDispatcher("/WEB-INF/admin/eligibility.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleEligibility ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleEligibility: " + e.getMessage(), e);
        }
    }

    private void handleAcademicReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleAcademicReport called");
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

            request.getRequestDispatcher("/WEB-INF/admin/academicReport.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleAcademicReport ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleAcademicReport: " + e.getMessage(), e);
        }
    }

    private void handleSendReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[AdminServlet] handleSendReport called");
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        try {
            com.crs.ejb.AcademicEJB academicEJB = new com.crs.ejb.AcademicEJB();
            academicEJB.sendAcademicReport(studentId);
            request.setAttribute("success", "Academic report sent successfully");
            request.setAttribute("currentPage", "academic-report");
            request.getRequestDispatcher("/WEB-INF/admin/academicReport.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("========== [AdminServlet] ERROR in handleSendReport ==========");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException("Error in handleSendReport: " + e.getMessage(), e);
        }
    }
}
