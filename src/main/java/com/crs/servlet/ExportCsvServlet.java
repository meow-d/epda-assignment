package com.crs.servlet;

import com.crs.ejb.AnalyticsEJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

@WebServlet("/admin/export-csv")
public class ExportCsvServlet extends HttpServlet {

    private AnalyticsEJB analyticsEJB = new AnalyticsEJB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication and authorization check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String exportType = request.getParameter("type");
        
        if (exportType == null) {
            exportType = "all";
        }

        // Set response headers for CSV download
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        
        // Set filename with timestamp
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String filename = "crs_analytics_" + timestamp + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");

        try {
            // Get analytics data
            Map<String, Object> systemAnalytics = analyticsEJB.getSystemAnalytics();
            Map<String, Object> academicAnalytics = analyticsEJB.getAcademicAnalytics();

            PrintWriter writer = response.getWriter();

            switch (exportType) {
                case "system":
                    exportSystemAnalytics(writer, systemAnalytics);
                    break;
                case "academic":
                    exportAcademicAnalytics(writer, academicAnalytics);
                    break;
                case "all":
                default:
                    exportAllAnalytics(writer, systemAnalytics, academicAnalytics);
                    break;
            }

            writer.flush();
            writer.close();

        } catch (Exception e) {
            throw new ServletException("Failed to export CSV: " + e.getMessage(), e);
        }
    }

    private void exportSystemAnalytics(PrintWriter writer, Map<String, Object> analytics) {
        writer.println("=== Course Recovery System - Analytics Export ===");
        writer.println("Generated: " + new java.util.Date());
        writer.println();
        
        writer.println("SYSTEM OVERVIEW");
        writer.println("Metric,Value");
        writer.println("Total Students," + getValue(analytics, "totalStudents"));
        writer.println("Total Users," + getValue(analytics, "totalUsers"));
        writer.println("Eligible Students," + getValue(analytics, "eligibleStudents"));
        writer.println("Ineligible Students," + getValue(analytics, "ineligibleStudents"));
        writer.println();
        
        writer.println("RECOVERY PERFORMANCE");
        writer.println("Metric,Value");
        writer.println("Active Recovery Plans," + getValue(analytics, "activeRecoveryPlans"));
        writer.println("Completed Recovery Plans," + getValue(analytics, "completedRecoveryPlans"));
        writer.println("Total Failed Courses," + getValue(analytics, "totalFailedCourses"));
        writer.println("Recovery Success Rate," + getValue(analytics, "recoverySuccessRate") + "%");
        writer.println();
    }

    private void exportAcademicAnalytics(PrintWriter writer, Map<String, Object> analytics) {
        writer.println("=== Course Recovery System - Academic Analytics ===");
        writer.println("Generated: " + new java.util.Date());
        writer.println();
        
        // CGPA Distribution
        writer.println("CGPA DISTRIBUTION");
        writer.println("Category,Count");
        @SuppressWarnings("unchecked")
        Map<String, Integer> cgpaDist = (Map<String, Integer>) analytics.get("cgpaDistribution");
        if (cgpaDist != null) {
            for (Map.Entry<String, Integer> entry : cgpaDist.entrySet()) {
                writer.println("\"" + entry.getKey() + "\"," + entry.getValue());
            }
        }
        writer.println();
        
        // Grade Distribution
        writer.println("GRADE DISTRIBUTION");
        writer.println("Grade,Count");
        @SuppressWarnings("unchecked")
        Map<String, Integer> gradeDist = (Map<String, Integer>) analytics.get("gradeDistribution");
        if (gradeDist != null) {
            for (Map.Entry<String, Integer> entry : gradeDist.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        }
        writer.println();
        
        // Failed Courses by Semester
        writer.println("FAILED COURSES BY SEMESTER");
        writer.println("Semester,Count");
        @SuppressWarnings("unchecked")
        Map<String, Integer> failedCourses = (Map<String, Integer>) analytics.get("failedCoursesBySemester");
        if (failedCourses != null) {
            for (Map.Entry<String, Integer> entry : failedCourses.entrySet()) {
                writer.println("\"" + entry.getKey() + "\"," + entry.getValue());
            }
        }
        writer.println();
    }

    private void exportAllAnalytics(PrintWriter writer, Map<String, Object> systemAnalytics, Map<String, Object> academicAnalytics) {
        exportSystemAnalytics(writer, systemAnalytics);
        exportAcademicAnalytics(writer, academicAnalytics);
    }

    private Object getValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return "0";
        }
        return value.toString();
    }
}
