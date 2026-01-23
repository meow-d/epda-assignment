<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Advanced Reports - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .reports-container { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin: 2rem 0; }
        .report-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .report-card h3 { margin-top: 0; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 0.5rem; }
        .chart-container { position: relative; height: 300px; margin: 1rem 0; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem; margin: 1rem 0; }
        .stat-item { text-align: center; padding: 1rem; background: #f8f9fa; border-radius: 6px; }
        .stat-value { font-size: 2rem; font-weight: bold; color: #007bff; display: block; }
        .stat-label { font-size: 0.9rem; color: #666; margin-top: 0.5rem; }
        .export-btn { margin-top: 1rem; }
        @media (max-width: 768px) { .reports-container { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/advanced-reports" class="active">Advanced Reports</a>
                <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <h2>Advanced Reports & Analytics</h2>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <div class="export-btn">
                <button onclick="window.print()" class="btn btn-secondary">Export as PDF</button>
                <button onclick="exportToCSV()" class="btn btn-secondary">Export Data</button>
            </div>

            <!-- System Overview -->
            <div class="reports-container">
                <div class="report-card">
                    <h3>System Overview</h3>
                    <div class="stats-grid">
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.totalStudents}</span>
                            <span class="stat-label">Total Students</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.totalUsers}</span>
                            <span class="stat-label">System Users</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.eligibleStudents}</span>
                            <span class="stat-label">Eligible Students</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.ineligibleStudents}</span>
                            <span class="stat-label">Ineligible Students</span>
                        </div>
                    </div>
                </div>

                <div class="report-card">
                    <h3>Recovery Performance</h3>
                    <div class="stats-grid">
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.activeRecoveryPlans}</span>
                            <span class="stat-label">Active Plans</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.completedRecoveryPlans}</span>
                            <span class="stat-label">Completed Plans</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.totalFailedCourses}</span>
                            <span class="stat-label">Failed Courses</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">${systemAnalytics.recoverySuccessRate != null ? String.format('%.1f', systemAnalytics.recoverySuccessRate) : '0.0'}%</span>
                            <span class="stat-label">Success Rate</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Charts Section -->
            <div class="reports-container">
                <div class="report-card">
                    <h3>CGPA Distribution</h3>
                    <div class="chart-container">
                        <canvas id="cgpaChart"></canvas>
                    </div>
                </div>

                <div class="report-card">
                    <h3>Grade Distribution</h3>
                    <div class="chart-container">
                        <canvas id="gradeChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- Failed Courses by Semester -->
            <div class="report-card">
                <h3>Failed Courses Trend</h3>
                <div class="chart-container" style="height: 400px;">
                    <canvas id="failedCoursesChart"></canvas>
                </div>
            </div>

            <!-- Recent Activity -->
            <div class="report-card">
                <h3>Recent Recovery Activity</h3>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Course</th>
                                <th>Task</th>
                                <th>Status</th>
                                <th>Deadline</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="plan" items="${systemAnalytics.recentRecoveryPlans}">
                                <tr>
                                    <td>${plan.value.studentName}</td>
                                    <td>${plan.value.courseTitle}</td>
                                    <td>${plan.value.task}</td>
                                    <td><span class="status-${plan.value.status}">${plan.value.status}</span></td>
                                    <td>${plan.value.deadline}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>

    <script>
        // CGPA Distribution Chart
        const cgpaData = ${academicAnalytics.cgpaDistribution != null ?
            academicAnalytics.cgpaDistribution.entrySet().toString().replace('=', ':') : '{}'};

        new Chart(document.getElementById('cgpaChart'), {
            type: 'pie',
            data: {
                labels: Object.keys(cgpaData),
                datasets: [{
                    data: Object.values(cgpaData),
                    backgroundColor: [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });

        // Grade Distribution Chart
        const gradeData = ${academicAnalytics.gradeDistribution != null ?
            academicAnalytics.gradeDistribution.entrySet().toString().replace('=', ':') : '{}'};

        new Chart(document.getElementById('gradeChart'), {
            type: 'bar',
            data: {
                labels: Object.keys(gradeData),
                datasets: [{
                    label: 'Number of Grades',
                    data: Object.values(gradeData),
                    backgroundColor: '#007bff',
                    borderColor: '#0056b3',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });

        // Failed Courses by Semester Chart
        const failedCoursesData = ${academicAnalytics.failedCoursesBySemester != null ?
            academicAnalytics.failedCoursesBySemester.entrySet().toString().replace('=', ':') : '{}'};

        new Chart(document.getElementById('failedCoursesChart'), {
            type: 'line',
            data: {
                labels: Object.keys(failedCoursesData),
                datasets: [{
                    label: 'Failed Courses',
                    data: Object.values(failedCoursesData),
                    borderColor: '#dc3545',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });

        // Export functionality
        function exportToCSV() {
            // Simple CSV export - in a real application, this would be more sophisticated
            alert('CSV export functionality would be implemented here');
        }
    </script>
</body>
</html>