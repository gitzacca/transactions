package br.com.pismo.transactions.application;

import br.com.pismo.transactions.domain.Transaction;
import br.com.pismo.transactions.domain.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {

    private TransactionService transactionService;

    @Autowired
    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/v1/transactions")
    public Transaction addTransaction(@RequestBody TransactionParameters parameters) {
        return transactionService.addTransaction(parameters.getAccountId(), parameters.getOperation(), parameters.getAmount());
    }
}
