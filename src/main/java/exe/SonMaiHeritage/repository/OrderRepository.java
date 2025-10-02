package exe.SonMaiHeritage.repository;

import exe.SonMaiHeritage.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderCode(String orderCode);
    
    List<Order> findByUserId(Integer userId);
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status IN ('CONFIRMED', 'SHIPPING', 'DELIVERED')")
    List<Order> findPaidOrders();
    
    long countByStatus(Order.OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN ('CONFIRMED', 'SHIPPING', 'DELIVERED')")
    Long sumTotalRevenue();
}
