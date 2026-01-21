<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Eligibility Check - Course Recovery System</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/officer/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/officer/recovery-plan">Recovery Plans</a>
                <a href="${pageContext.request.contextPath}/officer/academic-report">Academic Report</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <div class="eligibility">
                <h2>Student Eligibility Check</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>

                <div class="criteria-info">
                    <h3>Eligibility Criteria</h3>
                    <ul>
                        <li>CGPA must be at least 2.0</li>
                        <li>No more than 3 failed courses</li>
                    </ul>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Program</th>
                            <th>CGPA</th>
                            <th>Failed Courses</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="info" items="${eligibilityList}">
                            <tr class="${info.eligible ? 'eligible' : 'not-eligible'}">
                                <td>${info.student.id}</td>
                                <td>${info.student.name}</td>
                                <td>${info.student.program}</td>
                                <td>${info.cgpa}</td>
                                <td>${info.failedCourses}</td>
                                <td>
                                    <span class="${info.eligible ? 'status-success' : 'status-danger'}">
                                        ${info.eligible ? 'Eligible' : 'Not Eligible'}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${empty eligibilityList}">
                    <p class="no-data">No students found.</p>
                </c:if>

                <div class="summary">
                    <h3>Summary</h3>
                    <c:set var="eligibleCount" value="0"/>
                    <c:set var="notEligibleCount" value="0"/>
                    <c:forEach var="info" items="${eligibilityList}">
                        <c:if test="${info.eligible}">
                            <c:set var="eligibleCount" value="${eligibleCount + 1}"/>
                        </c:if>
                        <c:if test="${not info.eligible}">
                            <c:set var="notEligibleCount" value="${notEligibleCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    <p>Total Students: ${eligibilityList.size()}</p>
                    <p>Eligible: ${eligibleCount}</p>
                    <p>Not Eligible: ${notEligibleCount}</p>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
