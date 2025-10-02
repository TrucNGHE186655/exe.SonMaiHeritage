package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.controller.PaymentController;
import exe.SonMaiHeritage.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    Page<Payment> getAllPayments(Pageable pageable);
    Payment getPaymentById(Integer paymentId);
    Payment getPaymentByCode(String paymentCode);
    Payment getPaymentByVnpayTransactionId(String transactionId);
    List<Payment> getPaymentsByStatus(Payment.PaymentStatus status);
    List<Payment> getSuccessfulPayments();
    Payment getPaymentByOrderId(Integer orderId);
    PaymentController.PaymentStatistics getPaymentStatistics();
    Payment createPayment(Payment payment);
    Payment updatePaymentStatus(Integer paymentId, Payment.PaymentStatus status);
    List<Payment> getPaymentsByUserId(Integer userId);
}

