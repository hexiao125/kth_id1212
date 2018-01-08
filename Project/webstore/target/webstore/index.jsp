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
        <%
            if (session.getAttribute("login") != null) {
        %>
        <h2>Hello <%=session.getAttribute("username")%>, Welcome to Web Store!</h2>
        <hr>
        <p>You can log out and track order here.    
        <form action="logout.jsp">
            <button type="submit">Log out</button>
        </form>
        <form action="orderTrack.jsp">
            <button type="submit">Track orders</button>
        </form>
        <hr>
        <%
            } else {
        %>
        <h2>Welcome to Web Store!</h2>   
        <hr>
        <p>Please log in to place your order.    
        <p>Username: <input type="text" name="username"/>  
        <p>Password: <input type="text" name="password" />
        <button id="login">Log in</button>
        <hr>
        <p>Please register if you do not have an account.  
        <p>Username: <input type="text" name="newuser" /> 
        <p>Password: <input type="text" name="newpass" />
        <button id="register">Register</button>
        <hr>
        <%
            }
        %>

        <%-- first item --%>
        <h3>Marshall speaker</h3>
        <img src="pics/speaker.jpg" alt="speaker" height="200" width="200">
        <p>PRICE: 1200.00 SEK
        <%
            if (session.getAttribute("login") != null) {
        %>        
        <form action="cart.jsp">
            <p>QUANTITY: 
            <select name ="itemOneQuantity">
                <option value=1>1</option>
                <option value=2>2</option>
                <option value=3>3</option>
                <option value=4>4</option>
                <option value=5>5</option>
            </select>
            <p><input type="submit" value="Add to shopping cart">        
        </form> 
        <%
            }
        %> 
        <hr>
        
        <%-- second item --%>
        <h3>Iphone X</h3>
        <img src="pics/iphone.jpg" alt="iphone" height="200" width="200">
        <p>PRICE: 13200.00 SEK
        <%
            if (session.getAttribute("login") != null) {
        %>  
        <form action="cart.jsp">
            <p>QUANTITY:     
            <select name ="itemTwoQuantity">
                <option value=1>1</option>
                <option value=2>2</option>
                <option value=3>3</option>
                <option value=4>4</option>
                <option value=5>5</option>
            </select>
            <p><input type="submit" value="Add to shopping cart">
        </form>  
        <%
            }
        %> 
        <hr>
    </body>

</html>
