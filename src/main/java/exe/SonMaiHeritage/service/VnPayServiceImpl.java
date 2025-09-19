package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.config.VnPayConfig;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.VnPayResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
public class VnPayServiceImpl implements VnPayService {
    
    private final VnPayConfig vnPayConfig;
    
    public VnPayServiceImpl(VnPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }
    
    @Override
    public String createPaymentUrl(CheckoutRequest checkoutRequest, String paymentCode) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnPayConfig.getVersion());
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(checkoutRequest.getTotalAmount() * 100)); // VNPay expects amount in cents
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", paymentCode);
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + paymentCode);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnpParams.put("vnp_IpAddr", "127.0.0.1");
            
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnp_CreateDate);
            
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);
            
            // Sort params by key
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            
            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnPayConfig.getUrl() + "?" + queryUrl;
            
            log.info("VNPay Payment URL created: {}", paymentUrl);
            return paymentUrl;
            
        } catch (Exception e) {
            log.error("Error creating VNPay payment URL: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public VnPayResponse processPaymentReturn(Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_TxnRef = params.get("vnp_TxnRef");
        
        if ("00".equals(vnp_ResponseCode)) {
            return VnPayResponse.builder()
                    .success(true)
                    .paymentCode(vnp_TxnRef)
                    .message("Thanh toán thành công")
                    .build();
        } else {
            return VnPayResponse.builder()
                    .success(false)
                    .paymentCode(vnp_TxnRef)
                    .message("Thanh toán thất bại")
                    .build();
        }
    }
    
    @Override
    public boolean validateSignature(Map<String, String> params, String signature) {
        try {
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                if (!fieldName.equals("vnp_SecureHash") && !fieldName.equals("vnp_SecureHashType")) {
                    String fieldValue = params.get(fieldName);
                    if (fieldValue != null && fieldValue.length() > 0) {
                        hashData.append(fieldName);
                        hashData.append('=');
                        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                        hashData.append('&');
                    }
                }
            }
            
            // Remove the last '&'
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
            }
            
            String calculatedSignature = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            return calculatedSignature.equals(signature);
            
        } catch (Exception e) {
            log.error("Error validating signature: {}", e.getMessage());
            return false;
        }
    }
    
    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error creating HMAC SHA512: {}", e.getMessage());
            return null;
        }
    }
}
