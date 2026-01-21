<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Course Recovery System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-page">
            <h1>Error</h1>
            <p>An error has occurred.</p>
            <p class="error-message">${pageContext.exception}</p>
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">Return to Login</a>
        </div>
    </div>
</body>
</html>
