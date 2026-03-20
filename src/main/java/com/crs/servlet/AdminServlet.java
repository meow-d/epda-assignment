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
                request.setAttribute("currentPage", "add-user");
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
        request.setAttribute("currentPage", "dashboard");
        
        // Get analytics data
        try {
            var analytics = analyticsEJB.getSystemAnalytics();
            request.setAttribute("analytics", analytics);
        } catch (Exception e) {
            request.setAttribute("error", "Unable to load analytics: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<User> users = UserDAO.getAllUsers();
            request.setAttribute("users", users);
            request.setAttribute("currentPage", "users");
            request.getRequestDispatcher("/WEB-INF/admin/manageUsers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load users: " + e.getMessage());
            request.setAttribute("currentPage", "users");
            request.getRequestDispatcher("/WEB-INF/admin/manageUsers.jsp").forward(request, response);
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
                request.setAttribute("currentPage", "users");
                request.getRequestDispatcher("/WEB-INF/admin/editUser.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "User not found");
                request.setAttribute("currentPage", "users");
                handleListUsers(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load user: " + e.getMessage());
            request.setAttribute("currentPage", "users");
            handleListUsers(request, response);
        }
    }
}
