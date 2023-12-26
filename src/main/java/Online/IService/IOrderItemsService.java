package Online.IService;


import Online.Entity.Order;
import Online.Entity.OrderItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IOrderItemsService {

    public OrderItems addOrderItem(OrderItems orderItems);

    public  List<OrderItems> getAllOrderItems(UUID userId);

    Order createOrder(UUID userId, UUID shippingAddressId, String paymentMethod);

    Page<Order> getOrders(UUID userId, Pageable pageable);


}