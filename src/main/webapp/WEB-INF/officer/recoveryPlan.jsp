<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% request.setAttribute("pageTitle", "Recovery Plan - Course Recovery System"); %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
            <div class="recovery-plan">
                <h2>Course Recovery Plans</h2>
                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="success-message">${success}</div>
                </c:if>

                <div class="filter-section">
                    <label for="studentSelect">Select Student:</label>
                    <select id="studentSelect" name="studentId" onchange="window.location.href='recovery-plan?studentId=' + this.value">
                        <option value="">-- Select a Student --</option>
                        <c:forEach var="student" items="${students}">
                            <option value="${student.id}" ${not empty param.studentId && param.studentId == student.id ? 'selected' : ''}>${student.name} (ID: ${student.id})</option>
                        </c:forEach>
                    </select>
                </div>

                <c:if test="${not empty student}">
                    <div class="student-info">
                        <h3>Student Information</h3>
                        <p><strong>Name:</strong> ${student.name}</p>
                        <p><strong>Program:</strong> ${student.program}</p>
                        <p><strong>Email:</strong> ${student.email}</p>
                        <p><strong>Current CGPA:</strong> ${student.currentCgpa}</p>
                    </div>

                    <div class="failed-courses">
                        <h3>Failed Courses</h3>
                        <c:if test="${not empty failedCourses}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Course Code</th>
                                        <th>Semester</th>
                                        <th>Year</th>
                                        <th>Attempt</th>
                                        <th>Grade</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="grade" items="${failedCourses}">
                                        <tr>
                                            <td>${grade.courseCode}</td>
                                            <td>${grade.semester}</td>
                                            <td>${grade.year}</td>
                                            <td>${grade.attemptNo}</td>
                                            <td>${grade.grade}</td>
                                            <td>${grade.status}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                        <c:if test="${empty failedCourses}">
                            <p>No failed courses found for this student.</p>
                        </c:if>
                    </div>

                    <div class="recovery-plans-section">
                        <h3>Existing Recovery Plans</h3>
                        <c:if test="${not empty recoveryPlans}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Task</th>
                                        <th>Course</th>
                                        <th>Deadline</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="plan" items="${recoveryPlans}">
                                        <tr>
                                            <td>${plan.task}</td>
                                            <td>${plan.courseCode}</td>
                                            <td>${plan.deadline}</td>
                                            <td>${plan.status}</td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/officer/update-recovery-plan" method="post" style="display:inline;">
                                                    <input type="hidden" name="id" value="${plan.id}">
                                                    <select name="status">
                                                        <option value="active" ${plan.status == 'active' ? 'selected' : ''}>Active</option>
                                                        <option value="completed" ${plan.status == 'completed' ? 'selected' : ''}>Completed</option>
                                                        <option value="cancelled" ${plan.status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-sm">Update</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/officer/delete-recovery-plan" method="post" style="display:inline;">
                                                    <input type="hidden" name="id" value="${plan.id}">
                                                    <input type="hidden" name="studentId" value="${student.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?');">Delete</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                        <c:if test="${empty recoveryPlans}">
                            <p>No recovery plans found for this student.</p>
                        </c:if>

                        <div class="add-plan-form">
                            <h4>Add New Recovery Plan</h4>
                            <form action="${pageContext.request.contextPath}/officer/add-recovery-plan" method="post">
                                <input type="hidden" name="studentId" value="${student.id}">
                                <div class="form-group">
                                    <label for="courseCode">Course Code:</label>
                                    <input type="text" id="courseCode" name="courseCode" required>
                                </div>
                                <div class="form-group">
                                    <label for="task">Task:</label>
                                    <textarea id="task" name="task" rows="3" required></textarea>
                                </div>
                                <div class="form-group">
                                    <label for="deadline">Deadline:</label>
                                    <input type="datetime-local" id="deadline" name="deadline" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Add Recovery Plan</button>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>
<jsp:include page="/WEB-INF/includes/footer.jsp" />
