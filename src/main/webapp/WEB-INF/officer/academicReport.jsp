<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Academic Report - Course Recovery System</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Course Recovery System</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/officer/">Dashboard</a>
                <a href="${pageContext.request.contextPath}/officer/recovery-plan">Recovery Plans</a>
                <a href="${pageContext.request.contextPath}/officer/eligibility">Eligibility Check</a>
                <a href="${pageContext.request.contextPath}/officer/academic-report" class="active">Academic Report</a>
                <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
            </nav>
        </header>
        <main>
            <div class="academic-report">
                <h2>Academic Performance Report</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="success-message">${success}</div>
                </c:if>

                <div class="filter-section">
                    <label for="studentSelect">Select Student:</label>
                    <select id="studentSelect" name="studentId" onchange="window.location.href='academic-report?studentId=' + this.value">
                        <option value="">-- Select a Student --</option>
                        <c:forEach var="student" items="${students}">
                            <option value="${student.id}" ${not empty param.studentId && param.studentId == student.id ? 'selected' : ''}>${student.name} (ID: ${student.id})</option>
                        </c:forEach>
                    </select>
                </div>

                <c:if test="${not empty student}">
                    <div class="report-header">
                        <div class="student-details">
                            <h3>Student Details</h3>
                            <p><strong>Name:</strong> ${student.name}</p>
                            <p><strong>Student ID:</strong> ${student.id}</p>
                            <p><strong>Program:</strong> ${student.program}</p>
                            <p><strong>Email:</strong> ${student.email}</p>
                        </div>

                        <div class="cgpa-section">
                            <h3>Academic Performance</h3>
                            <p class="cgpa-value">Cumulative GPA (CGPA): <strong>${cgpa}</strong></p>
                        </div>
                    </div>

                    <div class="grades-section">
                        <h3>Grades</h3>
                        <c:if test="${not empty grades}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Course Code</th>
                                        <th>Course Title</th>
                                        <th>Credit Hours</th>
                                        <th>Semester</th>
                                        <th>Year</th>
                                        <th>Attempt</th>
                                        <th>Grade</th>
                                        <th>Grade Point</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="grade" items="${grades}">
                                        <tr>
                                            <td>${grade.courseCode}</td>
                                            <td>${grade.courseTitle}</td>
                                            <td>${grade.creditHours}</td>
                                            <td>${grade.semester}</td>
                                            <td>${grade.year}</td>
                                            <td>${grade.attemptNo}</td>
                                            <td>${grade.grade}</td>
                                            <td>${grade.gradePoint}</td>
                                            <td><span class="${grade.status == 'passed' ? 'status-success' : 'status-danger'}">${grade.status}</span></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                        <c:if test="${empty grades}">
                            <p class="no-data">No grades found for this student.</p>
                        </c:if>
                    </div>

                    <div class="actions">
                        <form action="${pageContext.request.contextPath}/officer/send-report" method="post">
                            <input type="hidden" name="studentId" value="${student.id}">
                            <button type="submit" class="btn btn-primary">Send Report via Email</button>
                        </form>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
</body>
</html>
