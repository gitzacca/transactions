package br.com.pismo.transactions.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

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
    public void mustAddAInstallmentTransaction() {
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

}
