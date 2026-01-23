<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.crs.util.CSRFUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
            </nav>
        </header>
        <main>
            <div class="forgot-password">
                <h2>Reset Password</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <c:if test="${not empty message}">
                    <div class="success-message">${message}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/auth/forgot-password" method="post" class="form-group">
                    <input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(request) %>">
                    <p>Enter your email address and we'll send you a link to reset your password.</p>

                    <label for="email">Email Address:</label>
                    <input type="email" id="email" name="email" required>

                    <button type="submit" class="btn btn-primary">Send Reset Link</button>
                </form>
                <p><a href="${pageContext.request.contextPath}/login.jsp">Back to Login</a></p>
            </div>
        </main>
    </div>
</body>
</html>