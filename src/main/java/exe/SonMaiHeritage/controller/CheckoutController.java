package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.VnPayResponse;
import exe.SonMaiHeritage.service.OrderService;
import exe.SonMaiHeritage.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@Log4j2
public class CheckoutController {
    
    private final VnPayService vnPayService;
    private final OrderService orderService;
    
    public CheckoutController(VnPayService vnPayService, OrderService orderService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
    }
    
    @PostMapping("/vnpay")
    public ResponseEntity<VnPayResponse> checkoutWithVnPay(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            log.info("Processing VNPay checkout for user: {}", checkoutRequest.getUserId());
            
            // Create order
            Order order = orderService.createOrder(checkoutRequest);
            
            // Generate payment code
            String paymentCode = "PAY" + System.currentTimeMillis() + "_" + order.getOrderCode();
            
            // Create payment URL
            String paymentUrl = vnPayService.createPaymentUrl(checkoutRequest, paymentCode);
            
            if (paymentUrl != null) {
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
            log.error("Error processing VNPay checkout: {}", e.getMessage());
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
                orderService.updateOrderStatus(orderCode, Order.OrderStatus.CONFIRMED);
                
                log.info("Payment successful for order: {}", orderCode);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment successful",
                        "orderCode", orderCode,
                        "transactionNo", params.get("vnp_TransactionNo")
                ));
            } else {
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
    
    @GetMapping("/order/{orderCode}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable String orderCode) {
        try {
            log.info("Fetching order details for code: {}", orderCode);
            Order order = orderService.getOrderByCode(orderCode);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order details: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
