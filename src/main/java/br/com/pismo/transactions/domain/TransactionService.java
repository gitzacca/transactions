package br.com.pismo.transactions.domain;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction addTransaction(Long accountId, OperationsTypes opeartion, BigDecimal amount);

    List<PaymentTracking> addPayments(List<Payment> payments);
}
