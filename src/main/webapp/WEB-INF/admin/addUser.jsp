<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add User - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admindash/">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admindash/users">Manage Users</a>
                <a href="${pageContext.request.contextPath}/admindash/add-user" class="active">Add User</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <h2>Add New User</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/admindash/add-user" method="post" class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>

                <label for="role">Role:</label>
                <select id="role" name="role" required>
                    <option value="admin">Admin</option>
                    <option value="officer">Officer</option>
                </select>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>

                <button type="submit" class="btn btn-primary">Add User</button>
            </form>
        </main>
    </div>
</body>
</html>