<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Officer - Course Recovery System'}</title>
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
                <a href="${pageContext.request.contextPath}/officer/" ${currentPage == 'dashboard' ? 'class="active"' : ''}>Dashboard</a>
                <a href="${pageContext.request.contextPath}/officer/recovery-plan" ${currentPage == 'recovery-plan' ? 'class="active"' : ''}>Recovery Plans</a>
                <a href="${pageContext.request.contextPath}/officer/eligibility" ${currentPage == 'eligibility' ? 'class="active"' : ''}>Eligibility Check</a>
                <a href="${pageContext.request.contextPath}/officer/academic-report" ${currentPage == 'academic-report' ? 'class="active"' : ''}>Academic Report</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
