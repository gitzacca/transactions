package br.com.pismo.transactions.domain;

import java.math.BigDecimal;

public class Payment {

    private Long accountId;
    private BigDecimal amount;

    public Payment(Long accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
