package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.entity.Payment;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.VnPayResponse;
import exe.SonMaiHeritage.service.OrderService;
import exe.SonMaiHeritage.service.PaymentService;
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
    private final PaymentService paymentService;
    
    public CheckoutController(VnPayService vnPayService, OrderService orderService, PaymentService paymentService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }
    
    @PostMapping("/vnpay")
    public ResponseEntity<VnPayResponse> checkoutWithVnPay(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            log.info("Processing VNPay checkout for user: {}", checkoutRequest.getUserId());
            
            // Create order
            Order order = orderService.createOrder(checkoutRequest);
            
            // Generate payment code
            String paymentCode = "PAY" + System.currentTimeMillis() + "_" + order.getOrderCode();
            
            // Create payment record first
            Payment payment = Payment.builder()
                    .order(order)
                    .paymentCode(paymentCode)
                    .amount(checkoutRequest.getTotalAmount())
                    .paymentMethod("VNPAY")
                    .status(Payment.PaymentStatus.PENDING)
                    .createdDate(java.time.LocalDateTime.now())
                    .updatedDate(java.time.LocalDateTime.now())
                    .build();
            
            paymentService.createPayment(payment);
            log.info("Created payment record with code: {}", paymentCode);
            
            // For testing - create a dummy payment URL since VNPay credentials are not configured
            String testPaymentUrl = "http://localhost:4200/payment-result?vnp_ResponseCode=00&vnp_TxnRef=" + paymentCode + "&vnp_TransactionNo=123456";
            
            return ResponseEntity.ok(VnPayResponse.builder()
                    .success(true)
                    .paymentUrl(testPaymentUrl)
                    .paymentCode(paymentCode)
                    .message("Payment URL created successfully (TEST MODE)")
                    .build());
            
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
            
            String txnRef = params.get("vnp_TxnRef");
            String transactionNo = params.get("vnp_TransactionNo");
            String responseCode = params.get("vnp_ResponseCode");
            
            // For test mode, skip signature validation
            boolean isSuccess = "00".equals(responseCode);
            
            if (isSuccess) {
                // Update order status to confirmed
                String orderCode = txnRef != null && txnRef.contains("_") ? txnRef.substring(txnRef.indexOf('_') + 1) : txnRef;
                orderService.updateOrderStatus(orderCode, Order.OrderStatus.CONFIRMED);
                
                // Update payment status and VNPay details
                try {
                    Payment payment = paymentService.getPaymentByCode(txnRef);
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setVnpayTransactionNo(transactionNo);
                    payment.setUpdatedDate(java.time.LocalDateTime.now());
                    paymentService.createPayment(payment); // This will update existing payment
                    log.info("Updated payment status to SUCCESS for code: {}", txnRef);
                } catch (Exception e) {
                    log.error("Error updating payment status: {}", e.getMessage());
                }
                
                log.info("Payment successful for order: {}", orderCode);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Thanh toán thành công",
                        "orderCode", orderCode,
                        "transactionNo", transactionNo,
                        "paymentCode", txnRef
                ));
            } else {
                // Update payment status to failed
                try {
                    Payment payment = paymentService.getPaymentByCode(txnRef);
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setVnpayTransactionNo(transactionNo);
                    payment.setUpdatedDate(java.time.LocalDateTime.now());
                    paymentService.createPayment(payment);
                    log.info("Updated payment status to FAILED for code: {}", txnRef);
                } catch (Exception e) {
                    log.error("Error updating payment status: {}", e.getMessage());
                }
                
                log.warn("Payment failed for order: {}", txnRef);
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Thanh toán thất bại",
                        "paymentCode", txnRef
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
