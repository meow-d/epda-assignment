<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.crs.util.CSRFUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Course Recovery System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="login-page">
    <div class="login-container">
        <div class="login-box">
            <h1>Course Recovery System</h1>
            <c:if test="${not empty param.error}">
                <div class="error-message">
                    <c:choose>
                        <c:when test="${param.error == 'auth'}">Access denied. Please log in.</c:when>
                        <c:otherwise>Invalid username or password</c:otherwise>
                    </c:choose>
                </div>
            </c:if>
            <c:if test="${not empty param.reset and param.reset == 'success'}">
                <div class="success-message">Password reset successful! Please log in with your new password.</div>
            </c:if>
            <form action="auth/login" method="post">
                <input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(request) %>">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary">Login</button>
            </form>
            <div class="forgot-password-link">
                <a href="${pageContext.request.contextPath}/forgotPassword.jsp">Forgot Password?</a>
            </div>
        </div>
    </div>
</body>
</html>
