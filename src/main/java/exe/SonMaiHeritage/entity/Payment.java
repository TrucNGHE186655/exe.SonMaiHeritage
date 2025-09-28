package exe.SonMaiHeritage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Column(name="payment_code", unique = true)
    private String paymentCode;
    
    @Column(name="amount")
    private Long amount;
    
    @Column(name="payment_method")
    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PaymentStatus status;
    
    @Column(name="vnpay_transaction_id")
    private String vnpayTransactionId;
    
    @Column(name="vnpay_transaction_no")
    private String vnpayTransactionNo;
    
    @Column(name="created_date")
    private LocalDateTime createdDate;
    
    @Column(name="updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name="payment_url",  columnDefinition = "TEXT")
    private String paymentUrl;
    
    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, CANCELLED
    }
}
