

package exe.SonMaiHeritage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private Long price;
    private String pictureUrl;
    private Integer quantity;
    private Integer typeId;
}