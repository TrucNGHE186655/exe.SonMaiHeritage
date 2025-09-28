package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.model.SessionBasketRequest;
import exe.SonMaiHeritage.model.SessionBasketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessionBasketServiceImpl implements SessionBasketService {
    
    // In-memory storage for session baskets (in production, use Redis or database)
    private final Map<String, SessionBasketResponse> sessionBaskets = new ConcurrentHashMap<>();
    private final ProductService productService;
    
    @Override
    public SessionBasketResponse getBasketBySessionId(String sessionId) {
        log.info("Getting basket for session: {}", sessionId);
        return sessionBaskets.getOrDefault(sessionId, createBasket(sessionId));
    }
    
    @Override
    public SessionBasketResponse addItemToBasket(SessionBasketRequest request) {
        log.info("Adding item to session basket: {}", request.getSessionId());
        
        // Check product availability
        if (!productService.checkProductAvailability(request.getProductId(), request.getQuantity())) {
            throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
        }
        
        SessionBasketResponse basket = sessionBaskets.getOrDefault(request.getSessionId(), createBasket(request.getSessionId()));
        
        // Check if item already exists
        boolean itemExists = false;
        for (SessionBasketResponse.SessionBasketItemResponse item : basket.getItems()) {
            if (item.getProductId().equals(request.getProductId())) {
                int newQuantity = item.getQuantity() + request.getQuantity();
                // Check availability for total quantity
                if (!productService.checkProductAvailability(request.getProductId(), newQuantity)) {
                    throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
                }
                item.setQuantity(newQuantity);
                item.setSubtotal(item.getProductPrice() * newQuantity);
                itemExists = true;
                break;
            }
        }
        
        if (!itemExists) {
            SessionBasketResponse.SessionBasketItemResponse newItem = SessionBasketResponse.SessionBasketItemResponse.builder()
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .productDescription(request.getProductDescription())
                    .productPrice(request.getProductPrice())
                    .productPictureUrl(request.getProductPictureUrl())
                    .productType(request.getProductType())
                    .quantity(request.getQuantity())
                    .subtotal(request.getProductPrice() * request.getQuantity())
                    .build();
            basket.getItems().add(newItem);
        }
        
        // Recalculate total
        basket.setTotalAmount(calculateTotal(basket.getItems()));
        
        sessionBaskets.put(request.getSessionId(), basket);
        log.info("Item added to session basket successfully");
        return basket;
    }
    
    @Override
    public SessionBasketResponse removeItemFromBasket(String sessionId, Integer productId) {
        log.info("Removing item from session basket: {}, product: {}", sessionId, productId);
        
        SessionBasketResponse basket = sessionBaskets.get(sessionId);
        if (basket != null) {
            basket.getItems().removeIf(item -> item.getProductId().equals(productId));
            basket.setTotalAmount(calculateTotal(basket.getItems()));
            sessionBaskets.put(sessionId, basket);
        }
        
        log.info("Item removed from session basket successfully");
        return basket;
    }
    
    @Override
    public SessionBasketResponse updateItemQuantity(String sessionId, Integer productId, Integer quantity) {
        log.info("Updating item quantity in session basket: {}, product: {}, quantity: {}", sessionId, productId, quantity);
        
        SessionBasketResponse basket = sessionBaskets.get(sessionId);
        if (basket != null) {
            for (SessionBasketResponse.SessionBasketItemResponse item : basket.getItems()) {
                if (item.getProductId().equals(productId)) {
                    if (quantity <= 0) {
                        basket.getItems().remove(item);
                    } else {
                        // Check product availability for new quantity
                        if (!productService.checkProductAvailability(productId, quantity)) {
                            throw new RuntimeException("Sản phẩm không đủ số lượng trong kho");
                        }
                        item.setQuantity(quantity);
                        item.setSubtotal(item.getProductPrice() * quantity);
                    }
                    break;
                }
            }
            basket.setTotalAmount(calculateTotal(basket.getItems()));
            sessionBaskets.put(sessionId, basket);
        }
        
        log.info("Item quantity updated in session basket successfully");
        return basket;
    }
    
    @Override
    public void clearBasket(String sessionId) {
        log.info("Clearing session basket: {}", sessionId);
        sessionBaskets.remove(sessionId);
        log.info("Session basket cleared successfully");
    }
    
    @Override
    public SessionBasketResponse createBasket(String sessionId) {
        log.info("Creating new session basket: {}", sessionId);
        SessionBasketResponse basket = SessionBasketResponse.builder()
                .sessionId(sessionId)
                .items(new ArrayList<>())
                .totalAmount(0L)
                .build();
        sessionBaskets.put(sessionId, basket);
        return basket;
    }
    
    private Long calculateTotal(List<SessionBasketResponse.SessionBasketItemResponse> items) {
        return items.stream()
                .mapToLong(SessionBasketResponse.SessionBasketItemResponse::getSubtotal)
                .sum();
    }
}
