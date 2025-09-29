package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.model.ProductResponse;
import exe.SonMaiHeritage.model.TypeResponse;
import exe.SonMaiHeritage.service.ProductService;
import exe.SonMaiHeritage.service.TypeService;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final TypeService typeService;

    public ProductController(ProductService productService, TypeService typeService) {
        this.productService = productService;
        this.typeService = typeService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer productId){
        ProductResponse productResponse = productService.getProductById(productId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProduct() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping()
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @PageableDefault(size = 10)Pageable pageable,
            @RequestParam(name="keyword", required = false) String keyword,
            @RequestParam(name="typeId", required = false) Integer typeId,
            @RequestParam(name="sort", defaultValue = "name") String sort,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ){
        Page<ProductResponse> productResponsePage;
        if(typeId!=null) {
            //search by type
            List<ProductResponse> productResponses = productService.searchProductsByType(typeId);
            productResponsePage = new PageImpl<>(productResponses, pageable, productResponses.size());
        }
        else if(keyword!=null && !keyword.isEmpty()){
            List<ProductResponse> productResponses = productService.searchProductsByName(keyword);
            productResponsePage = new PageImpl<>(productResponses, pageable, productResponses.size());
        }else{
            //If no search criteria, then retrieve based on sorting options
            Sort.Direction direction = "asc".equalsIgnoreCase(order)?Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sorting = Sort.by(direction, sort);

            productResponsePage = productService.getProducts(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting));
        }
        return new ResponseEntity<>(productResponsePage, HttpStatus.OK);
    }
    @GetMapping("/types")
    public ResponseEntity<List<TypeResponse>> getTypes(){
        List<TypeResponse> typeResponses = typeService.getAllTypes();
        return new ResponseEntity<>(typeResponses, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam("keyword") String keyword){
        List<ProductResponse> productResponses = productService.searchProductsByName(keyword);
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    // Admin endpoints đã được chuyển sang AdminController
    // Chỉ giữ lại các public endpoints cho khách hàng xem sản phẩm

}
