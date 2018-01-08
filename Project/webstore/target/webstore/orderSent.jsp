<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head><title>Web Store</title></head>
    <body bgcolor="peachpuff">
        <%
            session.setAttribute("itemOneQuan", 0);
            session.setAttribute("itemTwoQuan", 0);
        %>        
        
        <h2>Hello <%=session.getAttribute("username")%>, Welcome to Web Store!</h2>
        <hr>
        <h3>Your order has been paid successfully!</h3>
        <p>Order information:<br>
        <table border=1 cellpadding=5>
            <tr><td>Order ID</td><td><%=request.getParameter("orderId")%></td></tr>
            <tr><td>Item</td>
                <td>
                    <%
                        if (Integer.valueOf(request.getParameter("quanOne")) != 0) {
                    %>        
                    Speaker * <%=request.getParameter("quanOne")%> &nbsp&nbsp&nbsp       
                    <%
                        }
                        if (Integer.valueOf(request.getParameter("quanTwo")) != 0) {
                    %>        
                    IphoneX * <%=request.getParameter("quanTwo")%> &nbsp&nbsp&nbsp       
                    <%
                        }
                    %> 
                </td>
            </tr>
            <tr><td>Amount</td><td><%=request.getParameter("amount")%></td></tr>
            <tr><td>Bank</td><td><%=request.getParameter("bank")%></td></tr>
            <tr><td>Bank account</td><td><%=request.getParameter("bankAcc")%></td></tr>
            <tr><td>Mobile phone</td><td><%=request.getParameter("phone")%></td></tr>
            <tr><td>Post address</td><td><%=request.getParameter("address")%></td></tr>  
        </table><br>
        
        <form action="index.jsp">
            <button type="submit">Back to homepage</button>
        </form>
        <form action="orderTrack.jsp">
            <button type="submit">Track other orders</button>
        </form>
    </body>
</html>