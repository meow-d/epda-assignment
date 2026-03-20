<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% request.setAttribute("pageTitle", "Admin Dashboard - Course Recovery System"); %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
            <h2>Admin Dashboard</h2>
            <p>Welcome, Admin! Here's an overview of the system.</p>
            
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            
            <c:if test="${not empty analytics}">
                <div class="analytics-section">
                    <h3>System Analytics</h3>
                    <div class="analytics-grid">
                        <div class="metric-card">
                            <h4>Total Students</h4>
                            <div class="metric-value">${analytics.totalStudents}</div>
                        </div>
                        <div class="metric-card">
                            <h4>Eligible Students</h4>
                            <div class="metric-value">${analytics.eligibleStudents}</div>
                        </div>
                        <div class="metric-card">
                            <h4>Ineligible Students</h4>
                            <div class="metric-value">${analytics.ineligibleStudents}</div>
                        </div>
                        <div class="metric-card">
                            <h4>Active Recovery Plans</h4>
                            <div class="metric-value">${analytics.activeRecoveryPlans}</div>
                        </div>
                        <div class="metric-card">
                            <h4>Recovery Success Rate</h4>
                            <div class="metric-value">${String.format('%.1f%%', analytics.recoverySuccessRate)}</div>
                        </div>
                        <div class="metric-card">
                            <h4>Total Failed Courses</h4>
                            <div class="metric-value">${analytics.totalFailedCourses}</div>
                        </div>
                    </div>
                </div>
            </c:if>
            
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
                    <div class="card">
                        <h3>Manage Users</h3>
                        <p>Manage system users, roles, and permissions.</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
                    </div>
                </div>
            </div>
<jsp:include page="/WEB-INF/includes/footer.jsp" />
