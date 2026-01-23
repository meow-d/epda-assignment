package com.crs.servlet;

import com.crs.ejb.AcademicEJB;
import com.crs.ejb.AnalyticsEJB;
import com.crs.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/advanced-reports")
public class AdvancedReportServlet extends HttpServlet {

    private AcademicEJB academicEJB = new AcademicEJB();
    private AnalyticsEJB analyticsEJB = new AnalyticsEJB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Get analytics data
            Map<String, Object> systemAnalytics = analyticsEJB.getSystemAnalytics();
            Map<String, Object> academicAnalytics = analyticsEJB.getAcademicAnalytics();

            // Get student data for detailed reports
            List<Student> allStudents = academicEJB.getAllStudents();

            request.setAttribute("systemAnalytics", systemAnalytics);
            request.setAttribute("academicAnalytics", academicAnalytics);
            request.setAttribute("allStudents", allStudents);

            request.getRequestDispatcher("/WEB-INF/admin/advancedReports.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Failed to generate advanced reports: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/admin/advancedReports.jsp").forward(request, response);
        }
    }
}