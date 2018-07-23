package br.com.pismo.transactions.application;

import br.com.pismo.transactions.domain.*;
import br.com.pismo.transactions.external.HttpAccountLimitBalancer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockBean
    private HttpAccountLimitBalancer httpAccountLimitBalancer;

    @Test
    public void mustPerformAPostToAddACashPurchaseTransaction() throws Exception {
        String requestBody = "{\"accountId\": 1, \"operation\": \"CASH_PURCHASE\", \"amount\": 10}";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Transaction unpaidTransaction = transactionRepository.listUnpaidTransactionsBy(1L).get(0);

        Assert.assertTrue(unpaidTransaction.getBalance().compareTo(new BigDecimal(-10)) == 0);
        Assert.assertTrue(unpaidTransaction.getAmount().compareTo(new BigDecimal(-10)) == 0);
    }

    @Test
    public void mustPerformAPostToAddAnInstallmentPurchaseTransaction() throws Exception {
        String requestBody = "{\"accountId\": 1, \"operation\": \"INSTALLMENT_PURCHASE\", \"amount\": 10}";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Transaction unpaidTransaction = transactionRepository.listUnpaidTransactionsBy(1L).get(0);

        Assert.assertTrue(unpaidTransaction.getBalance().compareTo(new BigDecimal(-10)) == 0);
        Assert.assertTrue(unpaidTransaction.getAmount().compareTo(new BigDecimal(-10)) == 0);
    }

    @Test
    public void mustPerformAPostToAddAWithdrawalTransaction() throws Exception {
        String requestBody = "{\"accountId\": 1, \"operation\": \"WITHDRAWAL\", \"amount\": 10}";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Transaction unpaidTransaction = transactionRepository.listUnpaidTransactionsBy(1L).get(0);

        Assert.assertTrue(unpaidTransaction.getBalance().compareTo(new BigDecimal(-10)) == 0);
        Assert.assertTrue(unpaidTransaction.getAmount().compareTo(new BigDecimal(-10)) == 0);
    }
}
