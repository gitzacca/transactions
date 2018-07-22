package br.com.pismo.transactions.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.operation <> br.com.pismo.transactions.domain.OperationsTypes.PAYMENT and t.accountId = :accountId and t.balance < 0")
    List<Transaction> listUnpaidTransactionsBy(@Param("accountId") Long accountId);
}
