package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Payment;
import exe.SonMaiHeritage.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Override
    @Transactional
    public Payment createPayment(Payment payment) {
        log.info("Creating payment for order: {}", payment.getOrder().getId());
        payment.setCreatedDate(LocalDateTime.now());
        payment.setUpdatedDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public Payment updatePaymentStatus(String paymentCode, Payment.PaymentStatus status) {
        log.info("Updating payment status for code: {} to {}", paymentCode, status);
        Payment payment = paymentRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new RuntimeException("Payment not found with code: " + paymentCode));
        
        payment.setStatus(status);
        payment.setUpdatedDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment getPaymentByCode(String paymentCode) {
        log.info("Getting payment by code: {}", paymentCode);
        return paymentRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new RuntimeException("Payment not found with code: " + paymentCode));
    }
    
    @Override
    public Payment getPaymentByOrderId(Integer orderId) {
        log.info("Getting payment by order ID: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }
}
