package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.model.SessionBasketRequest;
import exe.SonMaiHeritage.model.SessionBasketResponse;
import exe.SonMaiHeritage.service.SessionBasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/session-basket")
@RequiredArgsConstructor
@Log4j2
public class SessionBasketController {
    
    private final SessionBasketService sessionBasketService;
    
    @GetMapping
    public ResponseEntity<SessionBasketResponse> getBasket(HttpServletRequest request) {
        try {
            String sessionId = getOrCreateSessionId(request);
            log.info("Fetching basket for session: {}", sessionId);
            SessionBasketResponse basket = sessionBasketService.getBasketBySessionId(sessionId);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            log.error("Error fetching basket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<SessionBasketResponse> addItemToBasket(
            @RequestBody SessionBasketRequest request,
            HttpServletRequest httpRequest) {
        try {
            String sessionId = getOrCreateSessionId(httpRequest);
            request.setSessionId(sessionId);
            
            log.info("Adding item to basket for session: {}, product: {}", sessionId, request.getProductId());
            SessionBasketResponse basket = sessionBasketService.addItemToBasket(request);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            log.error("Error adding item to basket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<SessionBasketResponse> removeItemFromBasket(
            @RequestParam Integer productId,
            HttpServletRequest request) {
        try {
            String sessionId = getOrCreateSessionId(request);
            log.info("Removing item from basket for session: {}, product: {}", sessionId, productId);
            SessionBasketResponse basket = sessionBasketService.removeItemFromBasket(sessionId, productId);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            log.error("Error removing item from basket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<SessionBasketResponse> updateItemQuantity(
            @RequestParam Integer productId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {
        try {
            String sessionId = getOrCreateSessionId(request);
            log.info("Updating item quantity in basket for session: {}, product: {}, quantity: {}", sessionId, productId, quantity);
            SessionBasketResponse basket = sessionBasketService.updateItemQuantity(sessionId, productId, quantity);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            log.error("Error updating item quantity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearBasket(HttpServletRequest request) {
        try {
            String sessionId = getOrCreateSessionId(request);
            log.info("Clearing basket for session: {}", sessionId);
            sessionBasketService.clearBasket(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing basket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String getOrCreateSessionId(HttpServletRequest request) {
        // Try to get session ID from request attribute first
        String sessionId = (String) request.getAttribute("sessionId");
        if (sessionId == null) {
            // Generate a new session ID
            sessionId = UUID.randomUUID().toString();
            request.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }
}
