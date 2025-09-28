package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Payment;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Payment updatePaymentStatus(String paymentCode, Payment.PaymentStatus status);
    Payment getPaymentByCode(String paymentCode);
    Payment getPaymentByOrderId(Integer orderId);
}
