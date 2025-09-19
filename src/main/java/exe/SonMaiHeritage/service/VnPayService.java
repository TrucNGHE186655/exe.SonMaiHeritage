package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.VnPayResponse;

import java.util.Map;

public interface VnPayService {
    String createPaymentUrl(CheckoutRequest checkoutRequest, String paymentCode);
    VnPayResponse processPaymentReturn(Map<String, String> params);
    boolean validateSignature(Map<String, String> params, String signature);
}
