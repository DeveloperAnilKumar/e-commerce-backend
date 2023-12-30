package Online.ServiceImpl;

import Online.EmailUtlity.EmailConfig;
import Online.Entity.*;
import Online.IService.IOrderItemsService;
import Online.IService.IProductService;
import Online.Repo.OrderItemsRepository;
import Online.Repo.OrderRepository;
import Online.Repo.ProductRepository;
import Online.Repo.ShippingAddressRepository;
import Online.enums.DeliveryStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
public class OrderItemsService implements IOrderItemsService {


    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ShippingAddressRepository shippingAddressRepository;


    @Autowired
    private  EmailConfig emailConfig;

    private  static final String KEY_ID="rzp_test_8gm05gh8gaDVho";

    private  static final String KEY_SECRET= "c0qhvqpDKANfEfSGHQ5yq0ap";

    @Override
    public OrderItems addOrderItem(OrderItems orderItem) {
        OrderItems existingOrderItem = orderItemsRepository.findByProductIdAndCompletedFalse(orderItem.getProduct().getId());
        return orderItemsRepository.save(Objects.requireNonNullElse(existingOrderItem, orderItem));
    }

    @Override
    public List<OrderItems> getAllOrderItems(UUID userId) {
        return orderItemsRepository.findAllByUserId(userId);
    }

    @Override
    public Order createOrder(UUID userId, UUID shippingAddressId,String paymentMethod) {
        List<OrderItems> orderItems = orderItemsRepository.findByUserIdAndCompletedFalse(userId);

        if (orderItems.isEmpty()) {
            throw new RuntimeException("No items found for user: " + userId);
        }

        Optional<ShippingAddress> shippingAddressOptional = shippingAddressRepository.findById(shippingAddressId);

        if (shippingAddressOptional.isEmpty()) {
            throw new RuntimeException("Shipping address not found with ID: " + shippingAddressId);
        }

        ShippingAddress shippingAddress = shippingAddressOptional.get();
        Order order = new Order();


        double totalAmount = orderItems.stream()
                .mapToDouble(item -> {
                    item.setCompleted(true);
                    Product product = item.getProduct();
                    Seller seller = product.getSeller();
                    order.setSeller(seller);
                    int quantity = item.getQuantity();
                    double unitPrice = item.getUnitPrice();
                    int availableQuantity = product.getStock();

                    if (availableQuantity >= quantity) {
                        product.setStock(availableQuantity - quantity);
                        productRepository.save(product);
                        return quantity * unitPrice;
                    } else {
                        throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
                    }
                })
                .sum();
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        Optional<User> userOptional = userService.getUserByUuid(userId);
        userOptional.ifPresent(order::setUser);

        return orderRepository.save(order);
    }

    @Override
   public  Page<Order> getOrders(UUID userId, Pageable pageable){
        return  orderRepository.findByUserId(userId,pageable);
    }


    public String generateOrderConfirmationEmail(Order order) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 0;}")
                .append(".container { max-width: 600px; margin: 20px auto; padding: 20px; background: #f9f9f9; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}")
                .append("h2 { color: #007bff; margin-bottom: 20px; }")
                .append("p { margin: 0 0 10px; }")
                .append(".order-details { background: #fff; border: 1px solid #ddd; border-radius: 6px; padding: 15px; margin-bottom: 20px; }")
                .append(".product-info { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 15px; }")
                .append("img { max-width: 100%; height: auto; }")
                .append("</style></head><body>")
                .append("<div class='container'>")
                .append("<h2>Order Confirmation</h2>")
                .append("<p>Hello ").append(order.getUser().getFullName()).append(",</p>")
                .append("<p>Your order has been placed successfully!</p>")
                .append("<div class='order-details'>")
                .append("<p><strong>Order ID:</strong> ").append(order.getId()).append("</p>")
                .append("<p><strong>Total Amount:</strong> ₹ ").append(order.getTotalAmount()).append("</p>")
                .append("<div class='product-info'><strong>Ordered Products:</strong></div>");

        List<OrderItems> orderItems = order.getOrderItems();
        for (OrderItems item : orderItems) {
            emailContent.append("<div class='product-info'>")
                    .append("<p><strong>Product Name:</strong> ").append(item.getProduct().getProductName()).append("</p>")
                    .append("<p><strong>Price:</strong> ₹ ").append(item.getProduct().getPrice()).append("</p>")
                    .append("<p><strong>Quantity:</strong> ").append(item.getQuantity()).append("</p>")
                    .append("<img src='").append(item.getProduct().getImageUrl()).append("' alt='Product Image' style='max-width:100%; height:auto; height:150px;'/>")
                    .append("</div>");
        }
        emailContent.append("</div>")
                .append("<p>Thank you for shopping with us!</p>")
                .append("</div></body></html>");

        return emailContent.toString();
    }




    public boolean notification(Order order) {

        new Thread(() -> {
            boolean notificationSent = emailConfig.sendEmail(
                    order.getUser().getEmail(),
                    "Order Confirmation",
                    generateOrderConfirmationEmail(order)
            );

        }).start();

        return true;
    }



    public Integer getLength(UUID uuid) {
        List<OrderItems> allByUserId = orderItemsRepository.findAllByUserId(uuid);
        return allByUserId.size();
    }

    public boolean deleteOrderById(UUID uuid) {
        if (uuid != null) {
            orderItemsRepository.deleteById(uuid);
            return true;
        }
        return false;
    }


    public List<OrderItems> getAllPendingOrders(UUID userId) {
        return orderItemsRepository.findByUserIdAndCompletedFalse(userId);
    }


    public List<Order> getOrdersForSeller(UUID sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }


    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderById(UUID id){
        return orderRepository.findById(id);

    }


    public String createOrderAmount(Double amountInPaise) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(KEY_ID, KEY_SECRET);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise*100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "tex_12355");
            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);
            return order.toString(); // Return the order details as needed
        } catch (RazorpayException e) {
            e.printStackTrace();
            return "Error creating order";
        }
    }


    public void updateStatus(UUID orderId, DeliveryStatus status) {

        Optional<Order>  order = orderRepository.findById(orderId);

        if (order.isPresent()){
            Order order1 = order.get();

            order1.setId(orderId);
            order1.setStatus(status);
            order1.setOrderDate(LocalDate.now());

            orderRepository.save(order1);
        }

    }


}



