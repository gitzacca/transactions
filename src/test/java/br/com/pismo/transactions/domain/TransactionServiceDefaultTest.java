package br.com.pismo.transactions.domain;

import br.com.pismo.transactions.external.exceptions.InsufficientFundsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionServiceDefaultTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private PaymentTrackingRepository paymentTrackingRepository;
    @Mock private AccountLimitBalancer accountLimitBalancer;
    private TransactionServiceDefault service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new TransactionServiceDefault(transactionRepository, paymentTrackingRepository, accountLimitBalancer);
    }

    @Test
    public void mustAddACreditTransaction() {
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Mockito.doAnswer(invocation -> {
            Assert.assertNotNull(invocation.getArguments()[0]);
            Assert.assertEquals(LimitType.CREDIT, invocation.getArguments()[1]);
            Assert.assertEquals(new BigDecimal(-50), invocation.getArguments()[2]);
            return null;
        }).when(accountLimitBalancer).updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));

        Transaction transaction = service.addTransaction(1L, OperationsTypes.CASH_PURCHASE, new BigDecimal(50));

        Assert.assertEquals(new BigDecimal(-50), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(-50), transaction.getBalance());
    }

    @Test
    public void mustAddAWithdrawalTransaction() {
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Mockito.doAnswer(invocation -> {
            Assert.assertNotNull(invocation.getArguments()[0]);
            Assert.assertEquals(LimitType.WITHDRAWAL, invocation.getArguments()[1]);
            Assert.assertEquals(new BigDecimal(-50), invocation.getArguments()[2]);
            return null;
        }).when(accountLimitBalancer).updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));

        Transaction transaction = service.addTransaction(1L, OperationsTypes.WITHDRAWAL, new BigDecimal(50));

        Assert.assertEquals(new BigDecimal(-50), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(-50), transaction.getBalance());
    }

    @Test
    public void mustAddAnInstallmentTransaction() {
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Mockito.doAnswer(invocation -> {
            Assert.assertNotNull(invocation.getArguments()[0]);
            Assert.assertEquals(LimitType.CREDIT, invocation.getArguments()[1]);
            Assert.assertEquals(new BigDecimal(-50), invocation.getArguments()[2]);
            return null;
        }).when(accountLimitBalancer).updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));

        Transaction transaction = service.addTransaction(1L, OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(50));

        Assert.assertEquals(new BigDecimal(-50), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(-50), transaction.getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void mustThrowExceptionWhenInsufficientFundsForCreditTransaction() {
        Mockito.doThrow(InsufficientFundsException.class).when(accountLimitBalancer)
                .updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));
        service.addTransaction(1L, OperationsTypes.CASH_PURCHASE, new BigDecimal(50));
    }

    @Test(expected = InsufficientFundsException.class)
    public void mustThrowExceptionWhenInsufficientFundsForWithdrawalTransaction() {
        Mockito.doThrow(InsufficientFundsException.class).when(accountLimitBalancer)
                .updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));
        service.addTransaction(1L, OperationsTypes.WITHDRAWAL, new BigDecimal(50));
    }

    @Test(expected = InsufficientFundsException.class)
    public void mustThrowExceptionWhenInsufficientFundsForInstallmentTransaction() {
        Mockito.doThrow(InsufficientFundsException.class).when(accountLimitBalancer)
                .updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));
        service.addTransaction(1L, OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(50));
    }

    @Test
    public void mustAddAPaymentForCreditTransaction() {
        Transaction transaction = new Transaction(1L,OperationsTypes.CASH_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Payment payment = new Payment(1L, new BigDecimal(100));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), transaction.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
    }

    @Test
    public void mustAddAPaymentForAWithdrawTransaction() {
        Transaction transaction = new Transaction(1L,OperationsTypes.WITHDRAWAL, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Payment payment = new Payment(1L, new BigDecimal(100));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), transaction.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
    }

    @Test
    public void mustAddAPaymentForAnInstallmentTransaction() {
        Transaction transaction = new Transaction(1L,OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Payment payment = new Payment(1L, new BigDecimal(100));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), transaction.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
    }

    @Test
    public void mustAddAPartialPaymentForCreditTransaction() {
        Transaction transaction = new Transaction(1L,OperationsTypes.CASH_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Payment payment = new Payment(1L, new BigDecimal(70));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(-30), transaction.getBalance());
        Assert.assertEquals(new BigDecimal(70), paymentTrackings.get(0).getAmount());
    }

    @Test
    public void mustAddAPaymentGreaterThanCreditTransaction() {
        Transaction transaction = new Transaction(1L,OperationsTypes.CASH_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Payment payment = new Payment(1L, new BigDecimal(120));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), transaction.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
        Assert.assertEquals(new BigDecimal(20), payment.getAmount());
    }

    @Test
    public void mustUsePositiveCreditToReduceDebt() {
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);

        Transaction positiveTransaction = new Transaction(1L,OperationsTypes.PAYMENT, new BigDecimal(100), new BigDecimal(100), new Date(), new Date());
        Mockito.when(transactionRepository.listPositiveTransactionBy(1L)).thenReturn(Arrays.asList(positiveTransaction));

        Mockito.doAnswer(invocation -> {
            Assert.assertNotNull(invocation.getArguments()[0]);
            Assert.assertEquals(LimitType.CREDIT, invocation.getArguments()[1]);
            Assert.assertEquals(new BigDecimal(0), invocation.getArguments()[2]);
            return null;
        }).when(accountLimitBalancer).updateLimits(Mockito.anyLong(), Mockito.any(LimitType.class), Mockito.any(BigDecimal.class));

        Transaction transaction = service.addTransaction(1L, OperationsTypes.CASH_PURCHASE, new BigDecimal(50));
        Assert.assertEquals(new BigDecimal(-50), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(0), transaction.getBalance());
    }

    @Test
    public void mustPayTheDebtInOrderOfPriority() {
        //Prioridade = WITHDRAWAL, INSTALLMENT_PURCHASE, CASH_PURCHASE
        Transaction withdrawal = new Transaction(1L,OperationsTypes.WITHDRAWAL, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());
        Transaction installmentPurchase = new Transaction(1L,OperationsTypes.INSTALLMENT_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());
        Transaction cashPurchase = new Transaction(1L,OperationsTypes.CASH_PURCHASE, new BigDecimal(-100), new BigDecimal(-100), new Date(), new Date());

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);
        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(installmentPurchase, withdrawal, cashPurchase));

        Payment payment = new Payment(1L, new BigDecimal(100));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), withdrawal.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
    }

    @Test
    public void mustPayTheDebtInOrderOfDate() {
        Transaction transaction2018 = new Transaction(1L,OperationsTypes.WITHDRAWAL, new BigDecimal(-100), new BigDecimal(-100), getDate(2018, 01, 01), new Date());
        Transaction transaction2017 = new Transaction(1L,OperationsTypes.WITHDRAWAL, new BigDecimal(-100), new BigDecimal(-100), getDate(2017, 01, 01), new Date());
        Transaction transaction2016 = new Transaction(1L,OperationsTypes.WITHDRAWAL, new BigDecimal(-100), new BigDecimal(-100), getDate(2016, 01, 01), new Date());

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).then(invocation -> invocation.getArguments()[0]);
        Mockito.when(transactionRepository.listUnpaidTransactionsBy(1L)).thenReturn(Arrays.asList(transaction2018, transaction2016, transaction2017));

        Payment payment = new Payment(1L, new BigDecimal(100));
        List<PaymentTracking> paymentTrackings = service.addPayments(Arrays.asList(payment));

        Assert.assertEquals(new BigDecimal(0), transaction2016.getBalance());
        Assert.assertEquals(new BigDecimal(100), paymentTrackings.get(0).getAmount());
    }

    private Date getDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTime();
    }

}
