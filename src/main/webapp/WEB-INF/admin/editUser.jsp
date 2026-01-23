<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit User - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <h2>Edit User</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/admin/update-user" method="post" class="form-group">
                <input type="hidden" name="id" value="${user.id}">

                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="${user.username}" required>

                <label for="password">New Password (leave blank to keep current):</label>
                <input type="password" id="password" name="password">

                <label for="role">Role:</label>
                <select id="role" name="role" required>
                    <option value="admin" ${user.role == 'admin' ? 'selected' : ''}>Admin</option>
                    <option value="officer" ${user.role == 'officer' ? 'selected' : ''}>Officer</option>
                </select>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${user.email}" required>

                <label for="status">Status:</label>
                <select id="status" name="status" required>
                    <option value="active" ${user.status == 'active' ? 'selected' : ''}>Active</option>
                    <option value="inactive" ${user.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                </select>

                <button type="submit" class="btn btn-primary">Update User</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Cancel</a>
            </form>
        </main>
    </div>
</body>
</html>