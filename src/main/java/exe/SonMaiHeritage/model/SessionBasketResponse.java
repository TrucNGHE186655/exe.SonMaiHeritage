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
public class SessionBasketResponse {
    private String sessionId;
    private List<SessionBasketItemResponse> items;
    private Long totalAmount;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SessionBasketItemResponse {
        private Integer productId;
        private String productName;
        private String productDescription;
        private Long productPrice;
        private String productPictureUrl;
        private String productType;
        private Integer quantity;
        private Long subtotal;
    }
}
