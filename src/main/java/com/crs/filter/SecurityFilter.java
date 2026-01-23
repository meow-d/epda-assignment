package com.crs.filter;

import com.crs.ejb.UserEJB;
import com.crs.model.User;
import com.crs.util.CSRFUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    private UserEJB userEJB;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize EJB - in a real application, this would be injected
        userEJB = new UserEJB();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow access to static resources, login page, and auth endpoints
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") ||
            path.equals("/login.jsp") || path.startsWith("/auth/") ||
            path.equals("/forgotPassword.jsp") || path.equals("/resetPassword.jsp") ||
            path.equals("/error.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        // Check role-based access
        String role = (String) session.getAttribute("role");
        if (role == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        // Admin-only areas
        if (path.startsWith("/admin/") && !"admin".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Admin role required");
            return;
        }

        // Officer-only areas
        if (path.startsWith("/officer/") && !"officer".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Officer role required");
            return;
        }

        // CSRF protection for POST requests
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String csrfToken = httpRequest.getParameter("csrfToken");
            if (!CSRFUtil.validateToken(httpRequest, csrfToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token validation failed");
                return;
            }
        }

        // Add security headers
        addSecurityHeaders(httpResponse);

        chain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Enable XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Prevent caching of sensitive pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // Content Security Policy (basic)
        response.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data:; " +
            "font-src 'self'; " +
            "form-action 'self'; " +
            "frame-ancestors 'none';");
    }

    @Override
    public void destroy() {
        // Cleanup resources if needed
    }
}