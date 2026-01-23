package com.crs.servlet;

import com.crs.dao.UserDAO;
import com.crs.ejb.AnalyticsEJB;
import com.crs.ejb.UserEJB;
import com.crs.model.User;
import com.crs.util.ValidationUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    // Direct instantiation to avoid CDI issues
    private UserEJB userEJB = new UserEJB();
    private AnalyticsEJB analyticsEJB = new AnalyticsEJB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            showDashboard(request, response);
            return;
        }

        switch (path) {
            case "/users":
                handleListUsers(request, response);
                break;
            case "/add-user":
                request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
                break;
            case "/edit-user":
                handleEditUserForm(request, response);
                break;
            default:
                showDashboard(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getPathInfo();

        if (path == null) {
                response.sendRedirect(request.getContextPath() + "/admin/");
            return;
        }

        switch (path) {
            case "/add-user":
                handleAddUser(request, response);
                break;
            case "/update-user":
                handleUpdateUser(request, response);
                break;
            case "/delete-user":
                handleDeleteUser(request, response);
                break;
            default:
            response.sendRedirect(request.getContextPath() + "/admin/");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String contextPath = request.getContextPath();

        // Get analytics data
        String analyticsHtml = "";
        try {
            var analytics = analyticsEJB.getSystemAnalytics();

            analyticsHtml = "<div class='analytics-section'>" +
                           "<h3>System Analytics</h3>" +
                           "<div class='analytics-grid'>" +
                           "<div class='metric-card'>" +
                           "<h4>Total Students</h4>" +
                           "<div class='metric-value'>" + analytics.get("totalStudents") + "</div>" +
                           "</div>" +
                           "<div class='metric-card'>" +
                           "<h4>Eligible Students</h4>" +
                           "<div class='metric-value'>" + analytics.get("eligibleStudents") + "</div>" +
                           "</div>" +
                           "<div class='metric-card'>" +
                           "<h4>Ineligible Students</h4>" +
                           "<div class='metric-value'>" + analytics.get("ineligibleStudents") + "</div>" +
                           "</div>" +
                           "<div class='metric-card'>" +
                           "<h4>Active Recovery Plans</h4>" +
                           "<div class='metric-value'>" + analytics.get("activeRecoveryPlans") + "</div>" +
                           "</div>" +
                           "<div class='metric-card'>" +
                           "<h4>Recovery Success Rate</h4>" +
                           "<div class='metric-value'>" + String.format("%.1f%%", analytics.get("recoverySuccessRate")) + "</div>" +
                           "</div>" +
                           "<div class='metric-card'>" +
                           "<h4>Total Failed Courses</h4>" +
                           "<div class='metric-value'>" + analytics.get("totalFailedCourses") + "</div>" +
                           "</div>" +
                           "</div>" +
                           "</div>";
        } catch (Exception e) {
            analyticsHtml = "<div class='error-message'>Unable to load analytics: " + e.getMessage() + "</div>";
        }

        String html = "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Admin Dashboard - Course Recovery System</title>" +
                      "<link rel='stylesheet' href='" + contextPath + "/css/style.css'>" +
                      "<style>" +
                      ".analytics-section { margin: 2rem 0; padding: 1rem; background: #f8f9fa; border-radius: 8px; }" +
                      ".analytics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-top: 1rem; }" +
                      ".metric-card { background: white; padding: 1rem; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; }" +
                      ".metric-card h4 { margin: 0 0 0.5rem 0; color: #666; font-size: 0.9rem; }" +
                      ".metric-value { font-size: 2rem; font-weight: bold; color: #007bff; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<header>" +
                      "<h1>Course Recovery System</h1>" +
                      "<nav>" +
                      "<a href='" + contextPath + "/admin/' class='active'>Dashboard</a>" +
                      "<a href='" + contextPath + "/admin/advanced-reports'>Advanced Reports</a>" +
                      "<a href='" + contextPath + "/officer/recovery-plan'>Recovery Plans</a>" +
                      "<a href='" + contextPath + "/officer/eligibility'>Eligibility Check</a>" +
                      "<a href='" + contextPath + "/officer/academic-report'>Academic Report</a>" +
                      "<a href='" + contextPath + "/admin/users'>Manage Users</a>" +
                      "<a href='" + contextPath + "/auth/logout'>Logout</a>" +
                      "</nav>" +
                      "</header>" +
                      "<main>" +
                      "<h2>Admin Dashboard</h2>" +
                      "<p>Welcome, Admin! Here's an overview of the system.</p>" +
                      analyticsHtml +
                      "<div class='dashboard'>" +
                      "<div class='cards'>" +
                      "<div class='card'>" +
                      "<h3>Recovery Plans</h3>" +
                      "<p>Manage student recovery plans and track progress.</p>" +
                      "<a href='" + contextPath + "/officer/recovery-plan' class='btn btn-primary'>View Recovery Plans</a>" +
                      "</div>" +
                      "<div class='card'>" +
                      "<h3>Eligibility Check</h3>" +
                      "<p>Check student eligibility for course recovery.</p>" +
                      "<a href='" + contextPath + "/officer/eligibility' class='btn btn-primary'>Check Eligibility</a>" +
                      "</div>" +
                      "<div class='card'>" +
                      "<h3>Academic Reports</h3>" +
                      "<p>Generate and send academic performance reports.</p>" +
                      "<a href='" + contextPath + "/officer/academic-report' class='btn btn-primary'>View Reports</a>" +
                      "</div>" +
                      "<div class='card'>" +
                      "<h3>Manage Users</h3>" +
                      "<p>Manage system users, roles, and permissions.</p>" +
                      "<a href='" + contextPath + "/admin/users' class='btn btn-primary'>Manage Users</a>" +
                      "</div>" +
                      "</div>" +
                      "</div>" +
                      "</main>" +
                      "</div>" +
                      "</body></html>";
        response.getWriter().write(html);
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<User> users = UserDAO.getAllUsers();
            response.setContentType("text/html");
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><title>Manage Users</title></head><body>");
            html.append("<h1>Manage Users</h1>");
            html.append("<table border='1'><tr><th>ID</th><th>Username</th><th>Role</th><th>Email</th><th>Status</th></tr>");

            for (User user : users) {
                html.append("<tr>")
                    .append("<td>").append(user.getId()).append("</td>")
                    .append("<td>").append(user.getUsername()).append("</td>")
                    .append("<td>").append(user.getRole()).append("</td>")
                    .append("<td>").append(user.getEmail()).append("</td>")
                    .append("<td>").append(user.getStatus()).append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
            html.append("<p><a href='" + request.getContextPath() + "/admin/'>Back to Dashboard</a></p>");
            html.append("</body></html>");
            response.getWriter().write(html.toString());
        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<!DOCTYPE html><html><head><title>Error</title></head><body><h1>Error</h1><p>Failed to load users: " + e.getMessage() + "</p></body></html>");
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            request.setAttribute("error", usernameValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!passwordValidation.isValid()) {
            request.setAttribute("error", passwordValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!emailValidation.isValid()) {
            request.setAttribute("error", emailValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!roleValidation.isValid()) {
            request.setAttribute("error", roleValidation.getErrorMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
            return;
        }
        if (!statusValidation.isValid()) {
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
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to add user: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/admin/addUser.jsp").forward(request, response);
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        // Validate user ID
        ValidationUtil.ValidationResult userIdValidation = ValidationUtil.validateRequired(userIdStr, "User ID");
        if (!userIdValidation.isValid()) {
            request.setAttribute("error", userIdValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
        } catch (NumberFormatException e) {
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
            request.setAttribute("error", usernameValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!emailValidation.isValid()) {
            request.setAttribute("error", emailValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!roleValidation.isValid()) {
            request.setAttribute("error", roleValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!statusValidation.isValid()) {
            request.setAttribute("error", statusValidation.getErrorMessage());
            handleListUsers(request, response);
            return;
        }
        if (!passwordValidation.isValid()) {
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
            }
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update user: " + e.getMessage());
            handleListUsers(request, response);
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO.deleteUser(userId);
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to delete user: " + e.getMessage());
            handleListUsers(request, response);
        }
    }

    private void handleEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));

        try {
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/admin/editUser.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "User not found");
                handleListUsers(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load user: " + e.getMessage());
            handleListUsers(request, response);
        }
    }
}
