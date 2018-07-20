package br.com.pismo.transactions.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments_tracking")
public class PaymentTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long creditTransactionId;

    @Column(nullable = false)
    private Long debitTransactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    public PaymentTracking(Long creditTransactionId, Long debitTransactionId, BigDecimal amount) {
        this.creditTransactionId = creditTransactionId;
        this.debitTransactionId = debitTransactionId;
        this.amount = amount;
    }

    protected PaymentTracking() {}

    public Long getId() {
        return id;
    }

    public Long getCreditTransactionId() {
        return creditTransactionId;
    }

    public Long getDebitTransactionId() {
        return debitTransactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
