package br.com.pismo.transactions.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceDefault implements TransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceDefault(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction addTransaction(Long accountId, OperationsTypes operation, BigDecimal amount) {

        Transaction transaction = new Transaction(accountId, operation, amount, amount, new Date(), new Date());

        if (!operation.equals(OperationsTypes.PAYMENT)) {
            List<Transaction> unpaidTransactions = transactionRepository.listUnpaidTransactionsBy(accountId);

            Comparator<Transaction> comparator = Comparator.comparing(t -> transaction.getOperation().getChargeOrder());
            comparator = comparator.thenComparing(t -> transaction.getEventDate());

            List<Transaction> unpaidTransactionsSorted = unpaidTransactions.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            for (Transaction transactionToBePaid : unpaidTransactionsSorted) {

                BigDecimal balance = transactionToBePaid.getBalance();
                balance = balance.add(amount);

                if (balance.signum() <= 0) {
                    //negativo (Pagamento menor que a divida) ou valor do pagamento igual a divida
                    transactionToBePaid.setBalance(balance);
                    PaymentTracking paymentTracking = new PaymentTracking(transaction.getId(), transactionToBePaid.getId(), amount);
                    break;

                } else {
                    //positivo (Pagamento maior que a divida)
                    transactionToBePaid.setBalance(new BigDecimal(0));
                    PaymentTracking paymentTracking = new PaymentTracking(transaction.getId(), transactionToBePaid.getId(), new BigDecimal(0));
                    amount = balance;
                }

            }

        } else {
            subtractAvailableCredit(accountId, operation, amount);
        }

        return transactionRepository.save(transaction);
    }

    private void addAvailableCredit(Long accountId, BigDecimal amount) {

    }

    private void subtractAvailableCredit(Long accountId, OperationsTypes operation, BigDecimal amount) {
        Request request = new Request();
        if (operation.equals(OperationsTypes.CASH_PURCHASE) || operation.equals(OperationsTypes.INSTALLMENT_PURCHASE)) {
            request.setAvailableCreditLimit(new CreditLimit(amount));
        } else {//OperationsTypes.WITHDRAWAL
            request.setAvailableWithdrawalLimit(new WithdrawalLimit(amount));
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.patchForObject("http://localhost:8080/v1/accounts/" + accountId, request, Void.class);
    }

    class Request {

        private CreditLimit availableCreditLimit;
        private WithdrawalLimit availableWithdrawalLimit;

        public CreditLimit getAvailableCreditLimit() {
            return availableCreditLimit;
        }

        public void setAvailableCreditLimit(CreditLimit availableCreditLimit) {
            this.availableCreditLimit = availableCreditLimit;
        }

        public WithdrawalLimit getAvailableWithdrawalLimit() {
            return availableWithdrawalLimit;
        }

        public void setAvailableWithdrawalLimit(WithdrawalLimit availableWithdrawalLimit) {
            this.availableWithdrawalLimit = availableWithdrawalLimit;
        }
    }

    class CreditLimit {
        private BigDecimal amount;

        public CreditLimit(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    class WithdrawalLimit {
        private BigDecimal amount;

        public WithdrawalLimit(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    public static void main(String[] args) {

//        Transaction transaction10 = new Transaction(1L, OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(10), new BigDecimal(10), new Date(2010, 01, 01),new Date(2010, 01, 01));
//        Transaction transaction0 = new Transaction(1L, OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(10), new BigDecimal(10), new Date(2017, 01, 01),new Date(2017, 01, 01));
//        Transaction transaction1 = new Transaction(1L, OperationsTypes.WITHDRAWAL, new BigDecimal(10), new BigDecimal(10), new Date(2016, 01, 01),new Date(2016, 01, 01));
//        Transaction transaction2 = new Transaction(1L, OperationsTypes.WITHDRAWAL, new BigDecimal(10), new BigDecimal(10), new Date(2014, 01, 01),new Date(2014, 01, 01));
//        Transaction transaction3 = new Transaction(1L, OperationsTypes.WITHDRAWAL, new BigDecimal(10), new BigDecimal(10), new Date(2015, 01, 01),new Date(2015, 01, 01));
//
//        Comparator<Transaction> comparator = Comparator.comparing(transaction -> transaction.getOperation().getChargeOrder());
//        comparator = comparator.thenComparing(transaction -> transaction.getEventDate());
//
//        List<Transaction> list = Arrays.asList(transaction0, transaction1, transaction10, transaction2, transaction3);
//
//        List<Transaction> unpaidTransactionsSorted = list.stream()
//                .sorted(comparator)
//                .collect(Collectors.toList());
//
//        System.out.println(unpaidTransactionsSorted);


        BigDecimal pagamento = new BigDecimal(100);
        BigDecimal divida = new BigDecimal(50);

        if (pagamento.compareTo(divida) > 0) {
            System.out.println("Pagamento maior que a divida");
        } else {
            System.out.println("Divida maior que o pagamento");
        }

    }

}
