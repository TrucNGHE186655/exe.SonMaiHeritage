package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.model.OrderResponse;
import exe.SonMaiHeritage.model.ProductRequest;
import exe.SonMaiHeritage.model.ProductResponse;
import exe.SonMaiHeritage.service.OrderService;
import exe.SonMaiHeritage.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {
    
    private final ProductService productService;
    private final OrderService orderService;
    
    // Product Management
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        try {
            log.info("Admin creating product: {}", request.getName());
            ProductResponse product = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Integer id, @RequestBody ProductRequest request) {
        try {
            log.info("Admin updating product: {}", id);
            ProductResponse product = productService.updateProduct(id, request);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        try {
            log.info("Admin deleting product: {}", id);
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        try {
            log.info("Admin fetching all orders");
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        try {
            log.info("Admin fetching order: {}", orderId);
            OrderResponse order = orderService.getOrderResponseById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/orders/statistics")
    public ResponseEntity<Object> getOrderStatistics() {
        try {
            log.info("Admin fetching order statistics");
            List<OrderResponse> orders = orderService.getAllOrders();
            
            long totalOrders = orders.size();
            long pendingOrders = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
            long confirmedOrders = orders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
            long deliveredOrders = orders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
            long cancelledOrders = orders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();
            
            long totalRevenue = orders.stream()
                    .filter(o -> "CONFIRMED".equals(o.getStatus()) || "DELIVERED".equals(o.getStatus()))
                    .mapToLong(OrderResponse::getTotalAmount)
                    .sum();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalOrders", totalOrders);
            statistics.put("pendingOrders", pendingOrders);
            statistics.put("confirmedOrders", confirmedOrders);
            statistics.put("deliveredOrders", deliveredOrders);
            statistics.put("cancelledOrders", cancelledOrders);
            statistics.put("totalRevenue", totalRevenue);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error fetching order statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
