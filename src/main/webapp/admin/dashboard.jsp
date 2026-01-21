<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Course Recovery System</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <div class="dashboard">
                <h2>Admin Dashboard</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="success-message">${success}</div>
                </c:if>
                <div class="cards">
                    <div class="card">
                        <h3>User Management</h3>
                        <p>Manage user accounts, roles, and permissions</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
                    </div>
                    <div class="card">
                        <h3>System Status</h3>
                        <p>View system status and statistics</p>
                        <a href="#" class="btn btn-secondary">View Status</a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
