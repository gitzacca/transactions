package br.com.pismo.transactions.application;

import br.com.pismo.transactions.domain.*;
import br.com.pismo.transactions.external.HttpAccountLimitBalancer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentTrackingRepository paymentTrackingRepository;

    @MockBean
    private HttpAccountLimitBalancer httpAccountLimitBalancer;

    @Test
    public void mustPerformAPostToAddAPayment() throws Exception {
        String requestBody = "[{\"accountId\": 1, \"amount\": 10}]";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Transaction> positiveTransactions = transactionRepository.listPositiveTransactionBy(1L);

        Assert.assertTrue(positiveTransactions.get(0).getAmount().compareTo(new BigDecimal(10)) == 0);
        Assert.assertTrue(positiveTransactions.get(0).getBalance().compareTo(new BigDecimal(10)) == 0);
    }

    @Test
    public void mustPerformAPostToPayADebit() throws Exception {
        Transaction transactionToBePaid = new Transaction(1L, OperationsTypes.CASH_PURCHASE,
                new BigDecimal(-10), new BigDecimal(-10), new Date(), new Date());

        transactionToBePaid = transactionRepository.save(transactionToBePaid);

        String requestBody = "[{\"accountId\": 1, \"amount\": 10}]";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Transaction transactionBeforePaid = transactionRepository.findById(transactionToBePaid.getId()).get();
        Assert.assertTrue(transactionBeforePaid.getBalance().compareTo(new BigDecimal(0)) == 0);

        PaymentTracking paymentTracking = paymentTrackingRepository.findAll().iterator().next();
        Assert.assertTrue(paymentTracking.getAmount().compareTo(new BigDecimal(10)) == 0);
    }

}
