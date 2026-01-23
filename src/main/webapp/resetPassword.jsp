<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - Course Recovery System</title>
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
            <div class="reset-password">
                <h2>Reset Your Password</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/auth/reset-password" method="post" class="form-group">
                    <input type="hidden" name="token" value="${param.token}">

                    <label for="password">New Password:</label>
                    <input type="password" id="password" name="password" required minlength="6">

                    <label for="confirmPassword">Confirm New Password:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">

                    <button type="submit" class="btn btn-primary">Reset Password</button>
                </form>
                <p><a href="${pageContext.request.contextPath}/login.jsp">Back to Login</a></p>
            </div>
        </main>
    </div>
    <script>
        // Client-side password confirmation validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match. Please try again.');
            }
        });
    </script>
</body>
</html>