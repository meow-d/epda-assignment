<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("currentPage", "dashboard"); %>
<% request.setAttribute("pageTitle", "Officer Dashboard - Course Recovery System"); %>
<jsp:include page="../includes/officer-header.jsp" />
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
<jsp:include page="../includes/officer-footer.jsp" />
