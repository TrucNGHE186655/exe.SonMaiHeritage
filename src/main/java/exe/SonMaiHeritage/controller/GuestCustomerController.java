package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.entity.GuestCustomer;
import exe.SonMaiHeritage.service.GuestCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest-customers")
@RequiredArgsConstructor
@Log4j2
public class GuestCustomerController {
    
    private final GuestCustomerService guestCustomerService;
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GuestCustomer> getGuestCustomerBySessionId(@PathVariable String sessionId) {
        try {
            log.info("Getting guest customer by session ID: {}", sessionId);
            GuestCustomer customer = guestCustomerService.getGuestCustomerBySessionId(sessionId);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            log.error("Error getting guest customer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<GuestCustomer> getGuestCustomerByEmail(@PathVariable String email) {
        try {
            log.info("Getting guest customer by email: {}", email);
            GuestCustomer customer = guestCustomerService.getGuestCustomerByEmail(email);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            log.error("Error getting guest customer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
