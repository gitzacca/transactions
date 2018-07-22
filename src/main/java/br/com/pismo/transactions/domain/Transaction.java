package br.com.pismo.transactions.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.ORDINAL)
    private OperationsTypes operation;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal  balance;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date eventDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dueDate;

    public Transaction(Long accountId, OperationsTypes operation, BigDecimal amount, BigDecimal balance, Date eventDate, Date dueDate) {
        this.accountId = accountId;
        this.operation = operation;
        this.amount = amount;
        this.balance = balance;
        this.eventDate = eventDate;
        this.dueDate = dueDate;
    }

    protected Transaction() {}

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public OperationsTypes getOperation() {
        return operation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Date getDueDate() {
        return dueDate;
    }
}
