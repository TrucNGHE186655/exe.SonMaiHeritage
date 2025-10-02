package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.controller.PaymentController;
import exe.SonMaiHeritage.entity.Payment;
import exe.SonMaiHeritage.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    @Override
    public Page<Payment> getAllPayments(Pageable pageable) {
        log.info("Retrieving all payments with pagination");
        return paymentRepository.findAll(pageable);
    }
    
    @Override
    public Payment getPaymentById(Integer paymentId) {
        log.info("Retrieving payment by ID: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
    }
    
    @Override
    public Payment getPaymentByCode(String paymentCode) {
        log.info("Retrieving payment by code: {}", paymentCode);
        return paymentRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new RuntimeException("Payment not found with code: " + paymentCode));
    }
    
    @Override
    public Payment getPaymentByVnpayTransactionId(String transactionId) {
        log.info("Retrieving payment by VNPay transaction ID: {}", transactionId);
        return paymentRepository.findByVnpayTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with VNPay transaction ID: " + transactionId));
    }
    
    @Override
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        log.info("Retrieving payments by status: {}", status);
        return paymentRepository.findByStatus(status);
    }
    
    @Override
    public List<Payment> getSuccessfulPayments() {
        log.info("Retrieving successful payments");
        return paymentRepository.findByStatus(Payment.PaymentStatus.SUCCESS);
    }
    
    @Override
    public Payment getPaymentByOrderId(Integer orderId) {
        log.info("Retrieving payment by order ID: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }
    
    @Override
    public PaymentController.PaymentStatistics getPaymentStatistics() {
        log.info("Calculating payment statistics");
        
        long totalPayments = paymentRepository.count();
        long successfulPayments = paymentRepository.countByStatus(Payment.PaymentStatus.SUCCESS);
        long failedPayments = paymentRepository.countByStatus(Payment.PaymentStatus.FAILED);
        long pendingPayments = paymentRepository.countByStatus(Payment.PaymentStatus.PENDING);
        
        Long totalAmount = paymentRepository.sumTotalAmount();
        Long successfulAmount = paymentRepository.sumAmountByStatus(Payment.PaymentStatus.SUCCESS);
        
        return new PaymentController.PaymentStatistics(
                totalPayments,
                successfulPayments,
                failedPayments,
                pendingPayments,
                totalAmount != null ? totalAmount : 0L,
                successfulAmount != null ? successfulAmount : 0L
        );
    }
    
    @Override
    public Payment createPayment(Payment payment) {
        log.info("Creating new payment with code: {}", payment.getPaymentCode());
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment updatePaymentStatus(Integer paymentId, Payment.PaymentStatus status) {
        log.info("Updating payment {} status to {}", paymentId, status);
        
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(status);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public List<Payment> getPaymentsByUserId(Integer userId) {
        log.info("Retrieving payments for user ID: {}", userId);
        return paymentRepository.findByOrderUserId(userId);
    }
}

