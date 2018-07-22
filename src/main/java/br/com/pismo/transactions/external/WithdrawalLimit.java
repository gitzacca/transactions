package br.com.pismo.transactions.external;

import java.math.BigDecimal;

public class WithdrawalLimit {

    private BigDecimal amount;

    public WithdrawalLimit(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
