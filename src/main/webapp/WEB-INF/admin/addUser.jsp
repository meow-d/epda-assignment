<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.crs.util.CSRFUtil" %>
<% request.setAttribute("currentPage", "add-user"); %>
<% request.setAttribute("pageTitle", "Add User - Course Recovery System"); %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
            <h2>Add New User</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/admin/add-user" method="post" class="form-group">
                <input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(request) %>">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>

                <label for="role">Role:</label>
                <select id="role" name="role" required>
                    <option value="admin">Admin</option>
                    <option value="officer">Officer</option>
                </select>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>

                <button type="submit" class="btn btn-primary">Add User</button>
            </form>
<jsp:include page="/WEB-INF/includes/footer.jsp" />
