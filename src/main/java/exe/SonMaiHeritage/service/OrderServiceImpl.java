package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.controller.OrderController;
import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.entity.OrderItem;
import exe.SonMaiHeritage.entity.User;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.repository.OrderRepository;
import exe.SonMaiHeritage.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Order createOrder(CheckoutRequest checkoutRequest) {
        log.info("Creating order for user: {}", checkoutRequest.getUserId());
        
        String orderCode = "ORD" + System.currentTimeMillis();
        
        List<OrderItem> orderItems = checkoutRequest.getItems().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getProductPrice() * item.getQuantity())
                        .productImage(item.getProductImage())
                        .build())
                .collect(Collectors.toList());
        
        User user = userRepository.findById(checkoutRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + checkoutRequest.getUserId()));

        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .totalAmount(checkoutRequest.getTotalAmount())
                .status(Order.OrderStatus.PENDING)
                .paymentMethod("VNPAY")
                .paymentStatus("PENDING")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .orderItems(orderItems)
                .shipFullName(checkoutRequest.getShipFullName())
                .shipPhone(checkoutRequest.getShipPhone())
                .shipStreet(checkoutRequest.getShipStreet())
                .shipWard(checkoutRequest.getShipWard())
                .shipDistrict(checkoutRequest.getShipDistrict())
                .shipProvince(checkoutRequest.getShipProvince())
                .build();
        
        // Set order reference in order items
        orderItems.forEach(item -> item.setOrder(order));
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with code: {}", savedOrder.getOrderCode());
        
        return savedOrder;
    }
    
    @Override
    public Order getOrderByCode(String orderCode) {
        log.info("Fetching order by code: {}", orderCode);
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found with code: " + orderCode));
    }
    
    @Override
    public void updateOrderStatus(String orderCode, Order.OrderStatus status) {
        log.info("Updating order status for code: {} to {}", orderCode, status);
        Order order = getOrderByCode(orderCode);
        order.setStatus(status);
        order.setUpdatedDate(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order status updated successfully");
    }
    
    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        log.info("Retrieving all orders with pagination");
        return orderRepository.findAll(pageable);
    }
    
    @Override
    public Order getOrderById(Integer orderId) {
        log.info("Retrieving order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }
    
    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        log.info("Retrieving orders for user ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        log.info("Retrieving orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> getPaidOrders() {
        log.info("Retrieving paid orders");
        return orderRepository.findPaidOrders();
    }
    
    @Override
    public Order updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        log.info("Updating order {} status to {}", orderId, status);
        Order order = getOrderById(orderId);
        order.setStatus(status);
        order.setUpdatedDate(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    @Override
    public OrderController.OrderStatistics getOrderStatistics() {
        log.info("Calculating order statistics");
        
        long totalOrders = orderRepository.count();
        long paidOrders = orderRepository.findPaidOrders().size();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
        
        Long totalRevenue = orderRepository.sumTotalRevenue();
        
        return new OrderController.OrderStatistics(
                totalOrders,
                paidOrders,
                pendingOrders,
                cancelledOrders,
                totalRevenue != null ? totalRevenue : 0L
        );
    }
}
