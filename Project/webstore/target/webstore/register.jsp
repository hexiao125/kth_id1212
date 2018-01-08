<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head><title>Web Store</title></head>
    <body bgcolor="peachpuff">
        <%
            if (request.getParameter("credtPass").equalsIgnoreCase("true") && session.getAttribute("login") == null) {
                session.setAttribute("login", true);
                session.setAttribute("username", request.getParameter("username"));
                session.setAttribute("itemOneQuan", 0);
                session.setAttribute("itemTwoQuan", 0);
            }
            if (session.getAttribute("login") != null) {
        %>
        <h2>Hello <%=session.getAttribute("username")%>, Welcome to Web Store!</h2>
        <hr>
        <h3>You are registered successfully!</h3>
        <form action="index.jsp">
            <button type="submit">Start shopping</button>
        </form>
        <%
            } else {
        %>
        <h2>Welcome to Web Store!</h2>
        <hr>
        <%
            if (request.getParameter("username")==null){
        %>        
        <h3> Not valid username or password, please specify valid username and password!</h3>
        <%
            } else {
        %>
        <h3>User <%=request.getParameter("username")%> exists! please try again with another username!</h3>
        <%
           }
        %>
        <form action="index.jsp">
            <button type="submit">Back to homepage</button>
        </form>
        <%
            }
        %>
    </body>
</html>
