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
public class SessionBasketRequest {
    private String sessionId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productDescription;
    private Long productPrice;
    private String productPictureUrl;
    private String productType;
}
