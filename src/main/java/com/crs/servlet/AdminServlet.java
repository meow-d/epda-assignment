package com.crs.servlet;

import com.crs.dao.UserDAO;
import com.crs.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
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
                request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
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
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        }
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<User> users = UserDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/admin/manageUsers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load users: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
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
