$(document).ready(function () {
  
    $("button#register").click(function () {
        var credt = {
            username: document.getElementsByName('newuser')[0].value,
            password: document.getElementsByName('newpass')[0].value
        };
        $.ajax({
            type: "POST",
            url: "webapi/resource/authen",
            data: JSON.stringify(credt),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
                window.location.href =  "register.jsp?credtPass="+data.credtPass+"&username="+data.username;
            },
            failure: function (errMsg) {
                alert(errMsg);
            }
        });
    });
    
    $("button#login").click(function () {
        $.ajax({
            type: "GET",
            url: "webapi/resource/authen?username="+document.getElementsByName('username')[0].value.toString()
                                      +"&password="+document.getElementsByName('password')[0].value.toString(),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
                window.location.href =  "login.jsp?credtPass="+data.credtPass+"&username="+data.username;
            },
            failure: function (errMsg) {
                alert(errMsg);
            }
        });
    });
    
    $("button#sendOrder").click(function () {
        var order = {
            orderId: document.getElementsByName('orderId')[0].value,
            quanOne: document.getElementsByName('quanOne')[0].value,
            quanTwo: document.getElementsByName('quanTwo')[0].value,
            amount: document.getElementsByName('amount')[0].value,
            username: document.getElementsByName('user')[0].value,
            bank: document.getElementsByName('bank')[0].value,
            bankAcc: document.getElementsByName('bankAccount')[0].value,
            phone: document.getElementsByName('phone')[0].value,
            address: document.getElementsByName('address')[0].value
        };
        $.ajax({
            type: "POST",
            url: "webapi/resource/order",
            data: JSON.stringify(order),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
                window.location.href =  "orderSent.jsp?orderId="+ data.orderId  +"&quanOne="+ data.quanOne
                                                    +"&quanTwo="+ data.quanTwo  +"&amount=" + data.amount 
                                                    +"&bank="   + data.bank     +"&bankAcc="+ data.bankAcc
                                                    +"&phone="  + data.phone    +"&address="+ data.address;
            },
            failure: function (errMsg) {
                alert(errMsg);
            }
        });
    });
    
    $("button#trackAllOrder").click(function () {
        $.ajax({
            type: "GET",
            url: "webapi/resource/order?username="+ document.getElementsByName('user')[0].value.toString()
                                      +"&orderId=all",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
               if(data[0] == null){
                   document.getElementById('displayOrder').innerHTML = "<h2>No order has been found for you!";
               } else {
                   document.getElementById('displayOrder').innerHTML = displayOrder(data);
               }
            },
            failure: function (errMsg) {
                alert(errMsg);
            }
        });
    });  
    
    $("button#trackSingleOrder").click(function () {
        $.ajax({
            type: "GET",
            url: "webapi/resource/order?username="+ document.getElementsByName('user')[0].value.toString()
                                      +"&orderId="+ document.getElementsByName('orderId')[0].value.toString(),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data) {
               if(data[0]==null){
                   document.getElementById('displayOrder').innerHTML = "<h2>No order has been found for you!";
               } else {
                   document.getElementById('displayOrder').innerHTML = displayOrder(data);
               }
            },
            failure: function (errMsg) {
                alert(errMsg);
            }
        });
    });  
    
    function displayOrder(data) {
        var contents = "<p>Order information:<br>";
        for (i = 0; i < data.length; i++) {
            contents += "<table border=1 cellpadding=5>"
                     + "<tr><td>Order ID</td><td>" + data[i].orderId + "</td></tr>"
                     + "<tr><td>Item</td><td>"; 
            if (data[i].quanOne > 0){
                contents += "Speaker * " + data[i].quanOne + "; ";
            }
            if (data[i].quanTwo > 0){
                contents += "IphoneX * " + data[i].quanTwo;
            }
            contents += "</td><tr>"
                     + "<tr><td>Amount</td><td>" + data[i].amount + "</td></tr><tr><td>Bank</td><td>" + data[i].bank + "</td><tr>"
                     + "<tr><td>Bank account</td><td>" + data[i].bankAcc + "</td><tr>"
                     + "<tr><td>Mobile phone</td><td>" + data[i].phone + "</td><tr>"
                     + "<tr><td>Post address</td><td>" + data[i].address + "</td><tr>"
                     + "</table><br><br>";
        }
        return contents;
    }

});
