package com.crs.servlet;

import com.crs.dao.UserDAO;
import com.crs.ejb.UserEJB;
import com.crs.model.User;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admindash/*")
public class AdminServlet extends HttpServlet {

    // Direct instantiation to avoid CDI issues
    private UserEJB userEJB = new UserEJB();

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
                request.getRequestDispatcher("/admin/addUser.jsp").forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admindash/index.jsp");
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
                response.sendRedirect(request.getContextPath() + "/admindash/index.jsp");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Return HTML directly - no JSP forwarding
        response.setContentType("text/html");
        String html = "<!DOCTYPE html><html><head><title>Admin Dashboard</title></head><body>" +
                     "<h1>Admin Dashboard</h1>" +
                     "<p>Welcome Admin!</p>" +
                     "<p><a href='" + request.getContextPath() + "/admindash/users'>Manage Users</a></p>" +
                     "<p><a href='" + request.getContextPath() + "/auth/logout'>Logout</a></p>" +
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
            html.append("<p><a href='" + request.getContextPath() + "/admindash/'>Back to Dashboard</a></p>");
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
        String status = "active";

        if (username == null || password == null || role == null || email == null ||
            username.isEmpty() || password.isEmpty() || role.isEmpty() || email.isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/admin/addUser.jsp").forward(request, response);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setEmail(email);
            user.setStatus(status);

            UserDAO.addUser(user);
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to add user: " + e.getMessage());
            request.getRequestDispatcher("/admin/addUser.jsp").forward(request, response);
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String email = request.getParameter("email");
        String status = request.getParameter("status");

        try {
            User user = UserDAO.getUserById(userId);
            if (user != null) {
                user.setUsername(username);
                if (password != null && !password.isEmpty()) {
                    user.setPassword(password);
                }
                user.setRole(role);
                user.setEmail(email);
                user.setStatus(status);

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
}
