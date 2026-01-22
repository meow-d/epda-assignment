<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <h2>Admin Dashboard</h2>
            <p>Welcome, Admin!</p>
            <div class="dashboard">
                <div class="cards">
                    <div class="card">
                        <h3>User Management</h3>
                        <p>Manage system users, roles, and permissions.</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
                    </div>
                    <div class="card">
                        <h3>System Status</h3>
                        <p>View system health and statistics.</p>
                        <p style="color: #7f8c8d; font-style: italic;">Coming Soon</p>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
