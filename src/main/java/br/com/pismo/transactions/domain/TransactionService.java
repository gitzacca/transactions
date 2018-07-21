package br.com.pismo.transactions.domain;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction addTransaction(Long accountId, OperationsTypes opeartion, BigDecimal amount);
}
