<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Officer Dashboard - Course Recovery System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/officer/" class="active">Dashboard</a>
                <a href="${pageContext.request.contextPath}/officer/recovery-plan">Recovery Plans</a>
                <a href="${pageContext.request.contextPath}/officer/eligibility">Eligibility Check</a>
                <a href="${pageContext.request.contextPath}/officer/academic-report">Academic Report</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <h2>Academic Officer Dashboard</h2>
            <p>Welcome, Academic Officer!</p>
            <div class="dashboard">
                <div class="cards">
                    <div class="card">
                        <h3>Recovery Plans</h3>
                        <p>Manage student recovery plans and track progress.</p>
                        <a href="${pageContext.request.contextPath}/officer/recovery-plan" class="btn btn-primary">View Recovery Plans</a>
                    </div>
                    <div class="card">
                        <h3>Eligibility Check</h3>
                        <p>Check student eligibility for course recovery.</p>
                        <a href="${pageContext.request.contextPath}/officer/eligibility" class="btn btn-primary">Check Eligibility</a>
                    </div>
                    <div class="card">
                        <h3>Academic Reports</h3>
                        <p>Generate and send academic performance reports.</p>
                        <a href="${pageContext.request.contextPath}/officer/academic-report" class="btn btn-primary">View Reports</a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>