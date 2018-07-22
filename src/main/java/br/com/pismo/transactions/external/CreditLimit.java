package br.com.pismo.transactions.external;

import java.math.BigDecimal;

public class CreditLimit {

    private BigDecimal amount;

    public CreditLimit(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
