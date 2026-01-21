package com.crs.servlet;

import com.crs.ejb.UserEJB;
import com.crs.model.User;

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

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Username and password are required");
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
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
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

    private String getRedirectPage(String role) {
        switch (role) {
            case "admin":
                return "/admin/dashboard.jsp";
            case "officer":
                return "/officer/dashboard.jsp";
            default:
                return "/login.jsp";
        }
    }
}
