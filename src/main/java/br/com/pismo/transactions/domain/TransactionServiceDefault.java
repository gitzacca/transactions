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

        BigDecimal originalAmount = amount;
        List<Transaction> positiveTransactions = transactionRepository.listPositiveTransactionBy(accountId);

        for (Transaction positiveTransaction : positiveTransactions) {

            if (positiveTransaction.getBalance().compareTo(amount) > 0) {
                BigDecimal newBalance = positiveTransaction.getBalance().subtract(amount);
                positiveTransaction.setBalance(newBalance);
                amount = new BigDecimal(0);
            } else {
                amount = amount.subtract(positiveTransaction.getBalance());
                positiveTransaction.setBalance(new BigDecimal(0));
            }

        }

        amount = amount.negate();
        Transaction transaction = new Transaction(accountId, operation, originalAmount.negate(), amount, new Date(), new Date());
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
                    transactionToBePaid.setBalance(balance);
                    PaymentTracking paymentTracking = new PaymentTracking(paymentTransaction.getId(), transactionToBePaid.getId(), payment.getAmount());
                    paymentsTracking.add(paymentTracking);
                    changeAvailableCredit(payment.getAccountId(), transactionToBePaid.getOperation(), payment.getAmount());
                    paymentTransaction.setBalance(new BigDecimal(0));
                    break;

                } else {
                    transactionToBePaid.setBalance(new BigDecimal(0));
                    PaymentTracking paymentTracking = new PaymentTracking(paymentTransaction.getId(), transactionToBePaid.getId(), transactionToBePaid.getAmount().negate());
                    paymentsTracking.add(paymentTracking);
                    payment.setAmount(balance);
                    changeAvailableCredit(payment.getAccountId(), transactionToBePaid.getOperation(), transactionToBePaid.getAmount());
                    paymentTransaction.setBalance(balance);
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

}
