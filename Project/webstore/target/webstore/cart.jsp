<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head><title>Web Store</title></head>
    <body bgcolor="peachpuff">
        <%  
            String quan1 = request.getParameter("itemOneQuantity");
            String quan2 = request.getParameter("itemTwoQuantity");
            Integer quan1Sum = Integer.valueOf(session.getAttribute("itemOneQuan").toString());
            Integer quan2Sum = Integer.valueOf(session.getAttribute("itemTwoQuan").toString());
            
            if (quan1 != null) {
                quan1Sum += Integer.valueOf(quan1);
                session.setAttribute("itemOneQuan", String.valueOf(quan1Sum));
            }
            if (quan2 != null) {
                quan2Sum += Integer.valueOf(quan2);
                session.setAttribute("itemTwoQuan", String.valueOf(quan2Sum));
            }
            String total1 = String.valueOf(quan1Sum * 1200);
            String total2 = String.valueOf(quan2Sum * 13200);
            String total = String.valueOf(Integer.valueOf(total1) + Integer.valueOf(total2));
            session.setAttribute("amount", total);
        %>
        <h2>Hello <%=session.getAttribute("username")%>, Welcome to Web Store!</h2><hr>
        <h3>My shopping cart:</h3>
        <table border=1 cellpadding=5>
            <tr>
                <th>Item</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Amount</th>
            </tr>
            <tr>
                <td>Speaker</td>
                <td>1200</td>
                <td><%= quan1Sum%></td>
                <td><%= total1%></td>
            </tr>
            <tr>
                <td>IphoneX</td>
                <td>13200</td>
                <td><%= quan2Sum%></td>
                <td><%= total2%></td>
            </tr>
            <tr>
                <td>Total</td>
                <td>-</td>
                <td>-</td>
                <td><%= total%></td>
            </tr>
        </table>
            
        <form action="index.jsp">
            <p><input type="submit" value="Add more items"> 
        </form>  
            
        <form action="payment.jsp">
            <p><input type="submit" value="Go to payment"> 
        </form>                
            
    </body>
</html>