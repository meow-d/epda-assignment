<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Officer Dashboard - Course Recovery System</title>
</head>
<body>
    <div style="padding: 20px;">
        <h1>Course Recovery System - Academic Officer Dashboard</h1>
        <p>Welcome, Academic Officer!</p>
        <nav>
            <a href="<%= request.getContextPath() %>/officer/recovery-plan">Recovery Plans</a> |
            <a href="<%= request.getContextPath() %>/officer/eligibility">Eligibility Check</a> |
            <a href="<%= request.getContextPath() %>/officer/academic-report">Academic Report</a> |
            <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </nav>
        <div style="margin-top: 20px;">
            <h2>Officer Functions</h2>
            <ul>
                <li><a href="<%= request.getContextPath() %>/officer/recovery-plan">Recovery Plans</a></li>
                <li><a href="<%= request.getContextPath() %>/officer/eligibility">Eligibility Check</a></li>
                <li><a href="<%= request.getContextPath() %>/officer/academic-report">Academic Reports</a></li>
            </ul>
        </div>
    </div>
</body>
</html>
