package exe.SonMaiHeritage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCheckoutRequest {
    private String sessionId;
    private Long totalAmount;
    private List<CartItemRequest> items;
    
    // Guest information
    private String guestEmail;
    private String guestPhone;
    private String guestFullName;
    
    // Shipping information
    private String shipFullName;
    private String shipPhone;
    private String shipStreet;
    private String shipWard;
    private String shipDistrict;
    private String shipProvince;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CartItemRequest {
        private Integer productId;
        private String productName;
        private String productDescription;
        private Long productPrice;
        private String productImage;
        private String productType;
        private Integer quantity;
    }
}
