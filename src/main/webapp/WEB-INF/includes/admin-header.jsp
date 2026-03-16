<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Admin - Course Recovery System'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <c:if test="${not empty customHead}">
        ${customHead}
    </c:if>
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/" ${currentPage == 'dashboard' ? 'class="active"' : ''}>Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/users" ${currentPage == 'users' ? 'class="active"' : ''}>Manage Users</a>
                <a href="${pageContext.request.contextPath}/admin/add-user" ${currentPage == 'add-user' ? 'class="active"' : ''}>Add User</a>
                <a href="${pageContext.request.contextPath}/admin/advanced-reports" ${currentPage == 'advanced-reports' ? 'class="active"' : ''}>Advanced Reports</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
