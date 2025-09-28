package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.GuestCustomer;
import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.entity.OrderItem;
import exe.SonMaiHeritage.entity.Payment;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.GuestCheckoutRequest;
import exe.SonMaiHeritage.model.OrderResponse;
import exe.SonMaiHeritage.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final GuestCustomerService guestCustomerService;
    
    public OrderServiceImpl(OrderRepository orderRepository, PaymentService paymentService, GuestCustomerService guestCustomerService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.guestCustomerService = guestCustomerService;
    }
    
    @Override
    public Order createGuestOrder(CheckoutRequest checkoutRequest, GuestCheckoutRequest guestInfo) {
        log.info("Creating guest order for session: {}", guestInfo.getSessionId());
        
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
        
        // Create or update guest customer
        GuestCustomer guestCustomer = guestCustomerService.createOrUpdateGuestCustomer(guestInfo);

        Order order = Order.builder()
                .orderCode(orderCode)
                .guestCustomer(guestCustomer)
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
        log.info("Guest order created successfully with code: {}", savedOrder.getOrderCode());
        
        return savedOrder;
    }
    
    @Override
    public Order getOrderById(int orderId) {
        log.info("Fetching order by id: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with code: " + orderId));
    }
    
    @Override
    public void updateOrderStatus(int orderCode, Order.OrderStatus status) {
        log.info("Updating order status for id: {} to {}", orderCode, status);
        Order order = getOrderById(orderCode);
        order.setStatus(status);
        order.setUpdatedDate(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order status updated successfully");
    }
    
    @Override
    public OrderResponse getOrderResponseById(int orderId) {
        log.info("Fetching order response by id: {}", orderId);
        Order order = getOrderById(orderId);
        return convertToOrderResponse(order);
    }
    
    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    private OrderResponse convertToOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        // Get payment information
        Payment payment = null;
        try {
            payment = paymentService.getPaymentByOrderId(order.getId());
        } catch (Exception e) {
            log.warn("No payment found for order: {}", order.getId());
        }
        
        // Convert order items
        List<OrderResponse.OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productName(item.getProductName())
                        .productDescription("") // OrderItem doesn't have description field
                        .productPrice(item.getProductPrice())
                        .productPictureUrl(item.getProductImage())
                        .productType("") // OrderItem doesn't have type field
                        .quantity(item.getQuantity())
                        .subtotal(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
        
        // Guest customer info
        Integer userId = null;
        String userFullName = "";
        
        if (order.getGuestCustomer() != null) {
            userId = order.getGuestCustomer().getId();
            userFullName = order.getGuestCustomer().getFullName();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(userId)
                .userFullName(userFullName)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .createdDate(order.getCreatedDate())
                .updatedDate(order.getUpdatedDate())
                .orderItems(orderItemResponses)
                .shipFullName(order.getShipFullName())
                .shipPhone(order.getShipPhone())
                .shipStreet(order.getShipStreet())
                .shipWard(order.getShipWard())
                .shipDistrict(order.getShipDistrict())
                .shipProvince(order.getShipProvince())
                .paymentCode(payment != null ? payment.getPaymentCode() : null)
                .vnpayTransactionId(payment != null ? payment.getVnpayTransactionId() : null)
                .vnpayTransactionNo(payment != null ? payment.getVnpayTransactionNo() : null)
                .build();
    }
}
