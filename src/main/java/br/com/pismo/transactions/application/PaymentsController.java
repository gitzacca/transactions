package br.com.pismo.transactions.application;

import br.com.pismo.transactions.domain.Payment;
import br.com.pismo.transactions.domain.PaymentTracking;
import br.com.pismo.transactions.domain.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentsController {

    private TransactionService transactionService;

    @Autowired
    public PaymentsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/v1/payments")
    public List<PaymentTracking> addPayments(@RequestBody List<Payment> payments) {
        return transactionService.addPayments(payments);
    }
}
