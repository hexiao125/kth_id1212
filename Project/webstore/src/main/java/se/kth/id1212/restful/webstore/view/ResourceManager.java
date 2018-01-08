package se.kth.id1212.restful.webstore.view;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import se.kth.id1212.restful.webstore.controller.Controller;
import se.kth.id1212.restful.webstore.integration.Credential;
import se.kth.id1212.restful.webstore.integration.OrderEntity;

@Path("/resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceManager {

    private final Controller contr = new Controller();
    
    @GET
    @Path("/authen")
    public Credential checkAuthen(
            @QueryParam("username") String username,
            @QueryParam("password") String password) {
        Credential credt = new Credential(username, password);
        Credential loginStatus = contr.checkCredt(credt);
        return loginStatus;
    }
    
    @POST
    @Path("/authen")
    public Credential createUser(Credential credt) {
        Credential registerStatus = contr.createUser(credt);
        return registerStatus;
    }

    @POST
    @Path("/order")
    public OrderEntity createOrder(OrderEntity order) {
        OrderEntity returnOrder = contr.createOrder(order);
        return returnOrder;
    }
    
    @GET
    @Path("/order")
    public List<OrderEntity> getOrder(
            @QueryParam("username") String username,
            @QueryParam("orderId") String orderId) {
        List<OrderEntity> orders = contr.getOrder(username, orderId);
        return orders;
    }
    
}
