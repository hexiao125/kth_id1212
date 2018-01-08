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
        <h3>Track orders</h3>
        <form action="index.jsp">
            <p><input type="submit" value="Back to homepage"> 
        </form><hr>  
        
        <p>Here you can keep track of all your orders.<br>  
        <p><button id="trackAllOrder">Track all my orders</button><hr>
        
        <p>Please specify the order id you want to track: <br>
            <input type="text" name="orderId">
            <input type="hidden" name="user" value="<%=session.getAttribute("username")%>">  
        <p><button id="trackSingleOrder">Track order by ID</button><hr>   
        
        <div id="displayOrder" name="displayOrder" />
        

    </body>
</html>