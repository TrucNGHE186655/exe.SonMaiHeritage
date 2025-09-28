package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.GuestCustomer;
import exe.SonMaiHeritage.model.GuestCheckoutRequest;
import exe.SonMaiHeritage.repository.GuestCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class GuestCustomerServiceImpl implements GuestCustomerService {
    
    private final GuestCustomerRepository guestCustomerRepository;
    
    @Override
    @Transactional
    public GuestCustomer createOrUpdateGuestCustomer(GuestCheckoutRequest guestInfo) {
        log.info("Creating or updating guest customer for session: {}", guestInfo.getSessionId());
        
        // Check if guest customer already exists
        GuestCustomer existingCustomer = guestCustomerRepository.findBySessionId(guestInfo.getSessionId())
                .orElse(null);
        
        if (existingCustomer != null) {
            // Update existing customer
            existingCustomer.setEmail(guestInfo.getGuestEmail());
            existingCustomer.setPhone(guestInfo.getGuestPhone());
            existingCustomer.setFullName(guestInfo.getGuestFullName());
            existingCustomer.setFirstName(extractFirstName(guestInfo.getGuestFullName()));
            existingCustomer.setLastName(extractLastName(guestInfo.getGuestFullName()));
            existingCustomer.setUpdatedDate(LocalDateTime.now());
            
            GuestCustomer savedCustomer = guestCustomerRepository.save(existingCustomer);
            log.info("Guest customer updated successfully with ID: {}", savedCustomer.getId());
            return savedCustomer;
        } else {
            // Create new customer
            GuestCustomer newCustomer = GuestCustomer.builder()
                    .sessionId(guestInfo.getSessionId())
                    .email(guestInfo.getGuestEmail())
                    .phone(guestInfo.getGuestPhone())
                    .fullName(guestInfo.getGuestFullName())
                    .firstName(extractFirstName(guestInfo.getGuestFullName()))
                    .lastName(extractLastName(guestInfo.getGuestFullName()))
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();
            
            GuestCustomer savedCustomer = guestCustomerRepository.save(newCustomer);
            log.info("Guest customer created successfully with ID: {}", savedCustomer.getId());
            return savedCustomer;
        }
    }
    
    @Override
    public GuestCustomer getGuestCustomerBySessionId(String sessionId) {
        log.info("Getting guest customer by session ID: {}", sessionId);
        return guestCustomerRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Guest customer not found with session ID: " + sessionId));
    }
    
    @Override
    public GuestCustomer getGuestCustomerByEmail(String email) {
        log.info("Getting guest customer by email: {}", email);
        return guestCustomerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guest customer not found with email: " + email));
    }
    
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] names = fullName.trim().split("\\s+");
        return names[0];
    }
    
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] names = fullName.trim().split("\\s+");
        if (names.length > 1) {
            return fullName.substring(fullName.indexOf(" ") + 1);
        }
        return "";
    }
}
