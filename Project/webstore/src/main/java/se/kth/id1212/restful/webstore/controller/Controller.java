package se.kth.id1212.restful.webstore.controller;

import java.util.List;
import se.kth.id1212.restful.webstore.model.CredentialHandler;
import se.kth.id1212.restful.webstore.model.OrderHandler;
import se.kth.id1212.restful.webstore.integration.Credential;
import se.kth.id1212.restful.webstore.integration.OrderEntity;

public class Controller {
    private final CredentialHandler credtHD = new CredentialHandler();
    private final OrderHandler orderHD = new OrderHandler();
 
    public Controller(){
    }
    
    public Credential createUser(Credential credt){
        return credtHD.createUser(credt);
    }
        
    public Credential checkCredt(Credential credt){
        return credtHD.checkCredt(credt);
    }
    
    public OrderEntity createOrder(OrderEntity order){
        orderHD.createOrder(order);
        return order;
    }
    
    public List<OrderEntity> getOrder(String username, String orderId){
        return orderHD.getOrder(username, orderId);
    }
        
}
