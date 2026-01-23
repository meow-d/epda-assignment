package com.crs.servlet;

import com.crs.ejb.UserEJB;
import com.crs.model.User;
import com.crs.util.ValidationUtil;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    @EJB
    private UserEJB userEJB;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (path) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            case "/forgot-password":
                handleForgotPassword(request, response);
                break;
            case "/reset-password":
                handleResetPassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (path) {
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate inputs
        ValidationUtil.ValidationResult usernameValidation = ValidationUtil.validateRequired(username, "Username");
        ValidationUtil.ValidationResult passwordValidation = ValidationUtil.validateRequired(password, "Password");

        if (!usernameValidation.isValid()) {
            request.setAttribute("error", usernameValidation.getErrorMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        if (!passwordValidation.isValid()) {
            request.setAttribute("error", passwordValidation.getErrorMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userEJB.authenticateUser(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());

                String redirectPage = getRedirectPage(user.getRole());
                response.sendRedirect(request.getContextPath() + redirectPage);
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=true");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Login failed: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        // Validate email
        ValidationUtil.ValidationResult emailValidation = ValidationUtil.validateEmail(email);
        if (!emailValidation.isValid()) {
            request.setAttribute("error", emailValidation.getErrorMessage());
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = userEJB.requestPasswordReset(email.trim().toLowerCase());
            if (success) {
                request.setAttribute("message", "If an account with that email exists, a password reset link has been sent.");
            } else {
                // Don't reveal if email exists or not for security
                request.setAttribute("message", "If an account with that email exists, a password reset link has been sent.");
            }
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to process password reset request: " + e.getMessage());
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate token
        ValidationUtil.ValidationResult tokenValidation = ValidationUtil.validateRequired(token, "Reset token");
        if (!tokenValidation.isValid()) {
            request.setAttribute("error", tokenValidation.getErrorMessage());
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // Validate password
        ValidationUtil.ValidationResult passwordValidation = ValidationUtil.validatePassword(password);
        if (!passwordValidation.isValid()) {
            request.setAttribute("error", passwordValidation.getErrorMessage());
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = userEJB.resetPasswordWithToken(token.trim(), password.trim());
            if (success) {
                request.setAttribute("message", "Password reset successful! You can now log in with your new password.");
                response.sendRedirect(request.getContextPath() + "/login.jsp?reset=success");
            } else {
                request.setAttribute("error", "Invalid or expired reset token");
                request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Failed to reset password: " + e.getMessage());
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
        }
    }

    private String getRedirectPage(String role) {
        switch (role) {
            case "admin":
                return "/admin/";
            case "officer":
                return "/officer/";
            default:
                return "/login.jsp";
        }
    }
}
