package exe.SonMaiHeritage.repository;

import exe.SonMaiHeritage.entity.GuestCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestCustomerRepository extends JpaRepository<GuestCustomer, Integer> {
    Optional<GuestCustomer> findBySessionId(String sessionId);
    Optional<GuestCustomer> findByEmail(String email);
}
