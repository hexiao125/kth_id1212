package se.kth.id1212.restful.webstore.integration;

import java.util.HashMap;
import java.util.Map;

public class OrderDAO {
    private static Map<String, UserOrder> orderMap = new HashMap(); //key=username
    
    public OrderEntity createOrder(OrderEntity order){
        String username = order.getUsername();
        UserOrder userOrderDAO;
        if (orderMap.containsKey(username)){
            userOrderDAO = orderMap.get(username);
        } else {
            userOrderDAO = new UserOrder();
            orderMap.put(username, userOrderDAO);
        }
        OrderEntity returnOrder = userOrderDAO.createOrder(order);
        return returnOrder;
    }
    
    public Map<String, OrderEntity> getOrder(String username){        
        UserOrder userOrderDAO;
        if (orderMap.containsKey(username)){        
            userOrderDAO = orderMap.get(username);
        } else {
            userOrderDAO = new UserOrder();
        }
        Map<String, OrderEntity> orders = userOrderDAO.getOrder();
        return orders;
    }
    
}
