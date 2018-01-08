<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Web Store</title>
        <meta charset="UTF-8">
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="webstore.js"></script>
    </head>
    <body bgcolor="peachpuff"> 
        <h2>Hello <%=session.getAttribute("username")%>, Welcome to Web Store!</h2>
        <hr>
        <p>The total payment for your order is <%= session.getAttribute("amount")%> SEK.<br>
        <h3>Please fill in your bank and delivery information: </h3>
        <p>Bank: <br>
            <select name ="bank">
                <option value="Nordea">Nordea</option>
                <option value="Handelsbanken">Handelsbanken</option>
                <option value="SEB">SEB</option>
                <option value="Swebank">Swebank</option>
            </select>
        <p>Bank account: <br> <input type="text" name="bankAccount"> 
        <p>Mobile phone: <br> <input type="text" name="phone">
        <p>Post address: <br> <input type="text" name="address">
        <%
            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("MMddhhmmss");
            String preOrder = ft.format(dNow);
         %>
        <p><input type="hidden" name="orderId" value=<%=session.getAttribute("username")+preOrder%>>
        <p><input type="hidden" name="user" value=<%=session.getAttribute("username")%>>
        <p><input type="hidden" name="amount" value=<%=session.getAttribute("amount")%>>
        <p><input type="hidden" name="quanOne" value=<%=session.getAttribute("itemOneQuan")%>>
        <p><input type="hidden" name="quanTwo" value=<%=session.getAttribute("itemTwoQuan")%>>     
        <p><button id="sendOrder">Pay</button>
        <form action="cart.jsp">
            <p><input type="submit" value="Back"> 
        </form> 
    </body>
</html>
