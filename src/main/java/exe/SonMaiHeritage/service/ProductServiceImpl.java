package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Product;
import exe.SonMaiHeritage.entity.Type;
import exe.SonMaiHeritage.exceptions.ProductNotFoundException;
import exe.SonMaiHeritage.model.ProductRequest;
import exe.SonMaiHeritage.model.ProductResponse;
import exe.SonMaiHeritage.repository.ProductRepository;
import exe.SonMaiHeritage.repository.TypeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final TypeRepository typeRepository;

    public ProductServiceImpl(ProductRepository productRepository, TypeRepository typeRepository) {
        this.productRepository = productRepository;
        this.typeRepository = typeRepository;
    }

    @Override
    public ProductResponse getProductById(Integer productId) {
        log.info("Fetching Product by Id: {}", productId);
        Product product =productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("Product with given id doesn't exist"));
        ProductResponse productResponse = convertToProductResponse(product);
        log.info("Fetched Product by Id: {}", productId);
        return productResponse;
    }

    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {
        log.info("Fetching products");
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = productPage
                .map(this::convertToProductResponse);
        log.info("Fetched all products");
        return productResponses;
    }

    @Override
    public List<ProductResponse> searchProductsByName(String keyword) {
        log.info("Searching product(s) by name: {}", keyword);
        //Call the custom query Method
        List<Product> products = productRepository.searchByName(keyword);
        //Map
        List<ProductResponse> productResponses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        log.info("Fetched all products");
        return productResponses;
    }

    @Override
    public List<ProductResponse> searchProductsByType(Integer typeId) {
        log.info("Searching product(s) by typeId: {}", typeId);
        List<Product> products = productRepository.searchByType(typeId);
        List<ProductResponse> productResponses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        log.info("Fetched all products");
        return productResponses;
    }

    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .pictureUrl(product.getPictureUrl())
                .quantity(product.getQuantity())
                .productType(product.getType().getName())
                .build();
    }
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .pictureUrl(request.getPictureUrl())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .type(type)
                .build();

        Product saved = productRepository.save(product);
        return convertToProductResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        log.info("Updating product id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPictureUrl(request.getPictureUrl());
        product.setQuantity(request.getQuantity() != null ? request.getQuantity() : product.getQuantity());
        product.setType(type);

        Product updated = productRepository.save(product);
        return convertToProductResponse(updated);
    }

    @Override
    public void deleteProduct(Integer id) {
        log.info("Deleting product id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public boolean checkProductAvailability(Integer productId, Integer requestedQuantity) {
        log.info("Checking availability for product id: {}, requested quantity: {}", productId, requestedQuantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        boolean available = product.getQuantity() >= requestedQuantity;
        log.info("Product availability check result: {}", available);
        return available;
    }

    @Override
    public void updateProductQuantity(Integer productId, Integer quantityChange) {
        log.info("Updating quantity for product id: {}, quantity change: {}", productId, quantityChange);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        int newQuantity = product.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient product quantity");
        }
        
        product.setQuantity(newQuantity);
        productRepository.save(product);
        log.info("Updated product quantity to: {}", newQuantity);
    }

}
