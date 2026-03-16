<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("currentPage", "dashboard"); %>
<% request.setAttribute("pageTitle", "Admin Dashboard - Course Recovery System"); %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
            <h2>Admin Dashboard</h2>
            <p>Welcome, Admin!</p>
            <div class="dashboard">
                <div class="cards">
                    <div class="card">
                        <h3>User Management</h3>
                        <p>Manage system users, roles, and permissions.</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
                    </div>
                    <div class="card">
                        <h3>System Status</h3>
                        <p>View system health and statistics.</p>
                        <p style="color: #7f8c8d; font-style: italic;">Coming Soon</p>
                    </div>
                </div>
            </div>
<jsp:include page="/WEB-INF/includes/footer.jsp" />
