package se.kth.id1212.restful.webstore.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import se.kth.id1212.restful.webstore.integration.OrderDAO;
import se.kth.id1212.restful.webstore.integration.OrderEntity;

public class OrderHandler {
    private final OrderDAO orderDB = new OrderDAO();
     
    public OrderHandler(){
    }
    
    public OrderEntity createOrder(OrderEntity order){
        OrderEntity returnOrder = orderDB.createOrder(order);
        return returnOrder;
    }
    
    public List<OrderEntity> getOrder(String username, String orderId){
        Map<String, OrderEntity> orders = orderDB.getOrder(username);
        List<OrderEntity> returnOrders = new ArrayList<>();
        if(orderId.equalsIgnoreCase("all")){
            for (Map.Entry<String, OrderEntity> entry : orders.entrySet()) {
                returnOrders.add(entry.getValue());
            }
            return returnOrders;
        } else {
            returnOrders.add(orders.get(orderId));
            return returnOrders; 
        }
    }
    
}
