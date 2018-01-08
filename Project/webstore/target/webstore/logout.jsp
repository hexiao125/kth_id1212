<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head><title>Web Store</title></head>
    <body bgcolor="peachpuff">
        <%
            session.removeAttribute("login");
            session.removeAttribute("username");
            session.removeAttribute("itemOneQuan");
            session.removeAttribute("itemTwoQuan");
        %>
        <h2>Welcome to Web Store!</h2>
        <hr>
        <h3>You are logged out successfully!</h3>
        <form action="index.jsp">
            <button type="submit">Back to homepage</button>
        </form>
    </body>
</html>
