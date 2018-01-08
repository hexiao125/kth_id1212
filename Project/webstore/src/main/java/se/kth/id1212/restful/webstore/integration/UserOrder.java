package se.kth.id1212.restful.webstore.integration;

import java.util.HashMap;
import java.util.Map;

public class UserOrder {

    private static Map<String, OrderEntity> userOrderMap = new HashMap(); 
    
    public OrderEntity createOrder(OrderEntity order){
        String orderId = String.valueOf(order.getOrderId());
        userOrderMap.put(orderId, order);
        return userOrderMap.get(orderId);        
    }
    
    public Map<String, OrderEntity> getOrder(){
         return userOrderMap;
    }
    
}
