package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.entity.Payment;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.GuestCheckoutRequest;
import exe.SonMaiHeritage.model.OrderResponse;
import exe.SonMaiHeritage.model.VnPayResponse;
import exe.SonMaiHeritage.service.OrderService;
import exe.SonMaiHeritage.service.PaymentService;
import exe.SonMaiHeritage.service.ProductService;
import exe.SonMaiHeritage.service.SessionBasketService;
import exe.SonMaiHeritage.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@Log4j2
public class CheckoutController {
    
    private final VnPayService vnPayService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final SessionBasketService sessionBasketService;
    private final ProductService productService;
    
    public CheckoutController(VnPayService vnPayService, OrderService orderService, PaymentService paymentService, SessionBasketService sessionBasketService, ProductService productService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.sessionBasketService = sessionBasketService;
        this.productService = productService;
    }
    
    @PostMapping("/vnpay")
    public ResponseEntity<VnPayResponse> checkoutWithVnPay(@Valid @RequestBody GuestCheckoutRequest guestCheckoutRequest) {
        try {
            log.info("Processing VNPay guest checkout for session: {}", guestCheckoutRequest.getSessionId());
            
            // Check product availability before checkout
            for (var item : guestCheckoutRequest.getItems()) {
                if (!productService.checkProductAvailability(item.getProductId(), item.getQuantity())) {
                    return ResponseEntity.badRequest()
                            .body(VnPayResponse.builder()
                                    .success(false)
                                    .message("Sản phẩm " + item.getProductName() + " không đủ số lượng trong kho")
                                    .build());
                }
            }
            
            // Convert GuestCheckoutRequest to CheckoutRequest
            CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                    .userId(null) // No user ID for guest
                    .totalAmount(guestCheckoutRequest.getTotalAmount())
                    .items(guestCheckoutRequest.getItems().stream()
                            .map(item -> CheckoutRequest.CartItemRequest.builder()
                                    .productId(item.getProductId())
                                    .productName(item.getProductName())
                                    .productPrice(item.getProductPrice())
                                    .productImage(item.getProductImage())
                                    .quantity(item.getQuantity())
                                    .build())
                            .collect(java.util.stream.Collectors.toList()))
                    .shipFullName(guestCheckoutRequest.getShipFullName())
                    .shipPhone(guestCheckoutRequest.getShipPhone())
                    .shipStreet(guestCheckoutRequest.getShipStreet())
                    .shipWard(guestCheckoutRequest.getShipWard())
                    .shipDistrict(guestCheckoutRequest.getShipDistrict())
                    .shipProvince(guestCheckoutRequest.getShipProvince())
                    .build();
            
            // Create order for guest
            Order order = orderService.createGuestOrder(checkoutRequest, guestCheckoutRequest);
            
            // Generate payment code
            String paymentCode = "PAY" + System.currentTimeMillis() + "_" + order.getId();
            
            // Create payment URL
            String paymentUrl = vnPayService.createPaymentUrl(checkoutRequest, paymentCode);
            
            if (paymentUrl != null) {
                // Create payment record in database
                Payment payment = Payment.builder()
                        .order(order)
                        .paymentCode(paymentCode)
                        .amount(guestCheckoutRequest.getTotalAmount())
                        .paymentMethod("VNPAY")
                        .status(Payment.PaymentStatus.PENDING)
                        .paymentUrl(paymentUrl)
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now())
                        .build();
                
                paymentService.createPayment(payment);
                
                // Clear session basket after successful checkout initiation
                sessionBasketService.clearBasket(guestCheckoutRequest.getSessionId());
                
                return ResponseEntity.ok(VnPayResponse.builder()
                        .success(true)
                        .paymentUrl(paymentUrl)
                        .paymentCode(paymentCode)
                        .message("Payment URL created successfully")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(VnPayResponse.builder()
                                .success(false)
                                .message("Failed to create payment URL")
                                .build());
            }
            
        } catch (Exception e) {
            log.error("Error processing VNPay guest checkout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VnPayResponse.builder()
                            .success(false)
                            .message("Internal server error")
                            .build());
        }
    }
    
    @GetMapping("/vnpay/return")
    public ResponseEntity<Map<String, Object>> vnPayReturn(
            @RequestParam Map<String, String> params,
            HttpServletRequest request) {
        
        try {
            log.info("Processing VNPay return with params: {}", params);
            
            String vnp_SecureHash = params.get("vnp_SecureHash");
            boolean isValidSignature = vnPayService.validateSignature(params, vnp_SecureHash);
            
            if (!isValidSignature) {
                log.warn("Invalid VNPay signature");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid signature"));
            }
            
            VnPayResponse response = vnPayService.processPaymentReturn(params);
            
            if (response.isSuccess()) {
                // Update order status to confirmed
                String txnRef = params.get("vnp_TxnRef");
                // Our txnRef format is PAY{timestamp}_{orderCode}
                String orderCode = txnRef != null && txnRef.contains("_") ? txnRef.substring(txnRef.indexOf('_') + 1) : txnRef;
                orderService.updateOrderStatus(Integer.parseInt(orderCode), Order.OrderStatus.CONFIRMED);
                
                // Update payment status to success
                paymentService.updatePaymentStatus(txnRef, Payment.PaymentStatus.SUCCESS);
                
                // Update payment with VNPay transaction details
                Payment payment = paymentService.getPaymentByCode(txnRef);
                payment.setVnpayTransactionId(params.get("vnp_TransactionId"));
                payment.setVnpayTransactionNo(params.get("vnp_TransactionNo"));
                payment.setUpdatedDate(LocalDateTime.now());
                paymentService.createPayment(payment);
                
                // Update product quantities after successful payment
                Order order = orderService.getOrderById(Integer.parseInt(orderCode));
                for (var orderItem : order.getOrderItems()) {
                    productService.updateProductQuantity(orderItem.getProductId(), -orderItem.getQuantity());
                }
                
                log.info("Payment successful for order: {}", orderCode);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment successful",
                        "orderCode", orderCode,
                        "transactionNo", params.get("vnp_TransactionNo")
                ));
            } else {
                // Update payment status to failed
                String txnRef = params.get("vnp_TxnRef");
                if (txnRef != null) {
                    paymentService.updatePaymentStatus(txnRef, Payment.PaymentStatus.FAILED);
                }
                
                log.warn("Payment failed for order: {}", params.get("vnp_TxnRef"));
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Payment failed"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error processing VNPay return: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable int orderId) {
        try {
            log.info("Fetching order details for code: {}", orderId);
            Order order = orderService.getOrderById(orderId);
            OrderResponse orderResponse = convertToOrderResponse(order);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            log.error("Error fetching order details: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
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
                        .productDescription("") 
                        .productPrice(item.getProductPrice())
                        .productPictureUrl(item.getProductImage())
                        .productType("") 
                        .quantity(item.getQuantity())
                        .subtotal(item.getTotalPrice())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        
       
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
