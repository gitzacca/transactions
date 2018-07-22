package br.com.pismo.transactions.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceDefault implements TransactionService {

    private TransactionRepository transactionRepository;
    private PaymentTrackingRepository paymentTrackingRepository;
    private AccountLimitBalancer accountLimitBalancer;

    @Autowired
    public TransactionServiceDefault(TransactionRepository transactionRepository, PaymentTrackingRepository paymentTrackingRepository, AccountLimitBalancer accountLimitBalancer) {
        this.transactionRepository = transactionRepository;
        this.paymentTrackingRepository = paymentTrackingRepository;
        this.accountLimitBalancer = accountLimitBalancer;
    }

    @Override
    public Transaction addTransaction(Long accountId, OperationsTypes operation, BigDecimal amount) {
        amount = amount.negate();
        Transaction transaction = new Transaction(accountId, operation, amount, amount, new Date(), new Date());
        changeAvailableCredit(accountId, operation, amount);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<PaymentTracking> addPayments(List<Payment> payments) {
        List<PaymentTracking> paymentsTracking = new ArrayList<>();

        payments.forEach(payment -> {

            Transaction paymentTransaction = new Transaction(payment.getAccountId(), OperationsTypes.PAYMENT, payment.getAmount(), payment.getAmount(), new Date(), new Date());
            paymentTransaction = transactionRepository.save(paymentTransaction);

            List<Transaction> unpaidTransactions = transactionRepository.listUnpaidTransactionsBy(payment.getAccountId());

            Comparator<Transaction> comparator = Comparator.comparing(t -> t.getOperation().getChargeOrder());
            comparator = comparator.thenComparing(t -> t.getEventDate());

            List<Transaction> unpaidTransactionsSorted = unpaidTransactions.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            for (Transaction transactionToBePaid : unpaidTransactionsSorted) {

                BigDecimal balance = transactionToBePaid.getBalance();
                balance = balance.add(payment.getAmount());

                if (balance.signum() <= 0) {
                    //negativo (Pagamento menor que a divida) ou valor do pagamento igual a divida
                    transactionToBePaid.setBalance(balance);
                    PaymentTracking paymentTracking = new PaymentTracking(paymentTransaction.getId(), transactionToBePaid.getId(), payment.getAmount());
                    paymentsTracking.add(paymentTracking);
                    changeAvailableCredit(payment.getAccountId(), transactionToBePaid.getOperation(), payment.getAmount());
                    paymentTransaction.setBalance(new BigDecimal(0));
                    break;

                } else {
                    //positivo (Pagamento maior que a divida)
                    transactionToBePaid.setBalance(new BigDecimal(0));
                    PaymentTracking paymentTracking = new PaymentTracking(paymentTransaction.getId(), transactionToBePaid.getId(), new BigDecimal(0));
                    paymentsTracking.add(paymentTracking);
                    payment.setAmount(balance);
                    changeAvailableCredit(payment.getAccountId(), transactionToBePaid.getOperation(), transactionToBePaid.getAmount());
                }

            }

        });

        paymentTrackingRepository.saveAll(paymentsTracking);

        return paymentsTracking;
    }

    private void changeAvailableCredit(Long accountId, OperationsTypes operation, BigDecimal amount) {
        if (operation.equals(OperationsTypes.CASH_PURCHASE) || operation.equals(OperationsTypes.INSTALLMENT_PURCHASE)) {
            accountLimitBalancer.updateLimits(accountId, LimitType.CREDIT, amount);
        } else {
            accountLimitBalancer.updateLimits(accountId, LimitType.WITHDRAWAL, amount);
        }
    }

//    public static void main(String[] args) {
//
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
//
//
//        BigDecimal pagamento = new BigDecimal(100);
//        BigDecimal divida = new BigDecimal(50);
//
//        if (pagamento.compareTo(divida) > 0) {
//            System.out.println("Pagamento maior que a divida");
//        } else {
//            System.out.println("Divida maior que o pagamento");
//        }
//
//    }

}
