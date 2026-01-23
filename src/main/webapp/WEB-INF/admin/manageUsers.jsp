<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users - Course Recovery System</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/add-user" class="btn btn-primary">Add User</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <div class="manage-users">
                <h2>Manage Users</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.username}</td>
                                <td>${user.role}</td>
                                <td>${user.email}</td>
                                <td>${user.status}</td>
                                 <td>
                                     <a href="${pageContext.request.contextPath}/admin/edit-user?id=${user.id}" class="btn btn-secondary btn-sm">Edit</a>
                                     <form action="${pageContext.request.contextPath}/admin/delete-user" method="post" style="display:inline;">
                                         <input type="hidden" name="id" value="${user.id}">
                                         <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to deactivate this user?');">Deactivate</button>
                                     </form>
                                 </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${empty users}">
                    <p class="no-data">No users found.</p>
                </c:if>
            </div>
        </main>
    </div>
</body>
</html>
