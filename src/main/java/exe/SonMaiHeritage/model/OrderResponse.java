package exe.SonMaiHeritage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Integer id;
    private String orderCode;
    private Integer userId;
    private String userFullName;
    private Long totalAmount;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<OrderItemResponse> orderItems;
    
    // Shipping information
    private String shipFullName;
    private String shipPhone;
    private String shipStreet;
    private String shipWard;
    private String shipDistrict;
    private String shipProvince;
    
    // Payment information
    private String paymentCode;
    private String vnpayTransactionId;
    private String vnpayTransactionNo;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Integer id;
        private String productName;
        private String productDescription;
        private Long productPrice;
        private String productPictureUrl;
        private String productType;
        private Integer quantity;
        private Long subtotal;
    }
}
