package br.com.pismo.transactions.application;

import br.com.pismo.transactions.domain.OperationsTypes;

import java.math.BigDecimal;

public class TransactionParameters {

    private Long accountId;
    private OperationsTypes operation;
    private BigDecimal amount;

    public TransactionParameters(Long accountId, OperationsTypes operation, BigDecimal amount) {
        this.accountId = accountId;
        this.operation = operation;
        this.amount = amount;
    }

    protected TransactionParameters() {}

    public Long getAccountId() {
        return accountId;
    }

    public OperationsTypes getOperation() {
        return operation;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
