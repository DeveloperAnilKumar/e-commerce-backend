package Online.Controller;


import Online.Entity.Order;
import Online.Entity.OrderItems;
import Online.Repo.OrderItemsRepository;
import Online.ServiceImpl.OrderItemsService;
import Online.Utlity.ExcelView;
import Online.Utlity.PdfView;
import Online.enums.DeliveryStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.awt.print.Pageable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/orderItems")
public class OrderItemsController {




    @Autowired
    private OrderItemsService orderItemsService;

    @Autowired
    private  PdfView pdfView;



    @PostMapping
    public ResponseEntity<OrderItems> addOrderItems(@RequestBody OrderItems orderItems){
        return  new ResponseEntity<>(orderItemsService.addOrderItem(orderItems), HttpStatus.CREATED);
    }


    @GetMapping("/{userId}")
    public  ResponseEntity<?> getAllOrderDetails(@PathVariable UUID userId){
        return new ResponseEntity<>(orderItemsService.getAllOrderItems(userId), HttpStatus.OK);
    }



    @GetMapping("len/{userId}")
    public  ResponseEntity<?> getLen(@PathVariable UUID userId){
        return new ResponseEntity<>(orderItemsService.getLength(userId), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public  ResponseEntity<Boolean> deleteOrderItems(@PathVariable UUID id){
        return  new ResponseEntity<>( orderItemsService.deleteOrderById(id), HttpStatus.OK);

    }

    @PostMapping("/{uuid}/{shippingAddressId}/{paymentMethod}")
    public ResponseEntity<?> createOrder(@PathVariable UUID uuid, @PathVariable UUID shippingAddressId, @PathVariable String paymentMethod) {
        Order createdOrder = orderItemsService.createOrder(uuid,shippingAddressId, paymentMethod);

        if (createdOrder != null) {
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to create order. No order items found.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}/pending")
    public List<OrderItems> getPendingOrderItemsByUserId(@PathVariable UUID userId) {
        return orderItemsService.getAllPendingOrders(userId);
    }

    @GetMapping("allOrder/{sellerId}")
    public ResponseEntity<List<Order>> getOrdersForSeller(@PathVariable UUID sellerId) {
        List<Order> orders = orderItemsService.getOrdersForSeller(sellerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }



    @GetMapping("/export-orders/{sellerId}")
    public ModelAndView exportOrdersToExcel(@PathVariable UUID sellerId) {
        List<Order> orders = orderItemsService.getOrdersForSeller(sellerId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new ExcelView());
        modelAndView.addObject("orders", orders);
        return modelAndView;
    }



    @GetMapping("/generate/{sellerId}")
    public ResponseEntity<byte[]> generateAndDownloadPdf(@PathVariable UUID sellerId) {
        try {
            // Fetch orders using the OrderService (replace this with your logic)
            List<Order> orders = orderItemsService.getOrdersForSeller(sellerId);

            // Generate PDF
            ByteArrayOutputStream outputStream = pdfView.generatePdfFromOrders(orders);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "orders.pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("placed/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return orderItemsService.getOrdersByUserId(userId);
    }


    @GetMapping("order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        Optional<Order> order = orderItemsService.getOrderById(id);
        return order.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create-order/{amount}")
    public ResponseEntity<String> createOrderAmount(@PathVariable Double amount) {
        String result = orderItemsService.createOrderAmount(amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @PostMapping("/notify")
    public ResponseEntity<String> sendOrderConfirmationNotification(@RequestBody Order order) {
        boolean notificationSent = orderItemsService.notification(order);

        if (notificationSent) {
            return ResponseEntity.ok("Order confirmation notification sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send order confirmation notification.");
        }
    }


    @GetMapping("/page/{userId}")
    public ResponseEntity<Page<Order>> getAllOrders( @PathVariable UUID userId ,
                                                     @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size)
    {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC,"orderDate");
        Page<Order> orders = orderItemsService.getOrders(userId,  pageRequest);
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<String> updateDeliveryStatus(
            @PathVariable UUID orderId,
            @PathVariable DeliveryStatus status
    ) {
        try {
            orderItemsService.updateStatus(orderId, status);
            return ResponseEntity.ok("Delivery status updated successfully for order ID: " + orderId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update delivery status");
        }
    }





}
