package br.com.pismo.transactions.external;

import br.com.pismo.transactions.domain.AccountLimitBalancer;
import br.com.pismo.transactions.domain.LimitType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class HttpAccountLimitBalancer implements AccountLimitBalancer {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void updateLimits(Long accountId, LimitType limitType, BigDecimal amount) {
        Request request = new Request();

        if (limitType.equals(LimitType.CREDIT)) {
            request.setAvailableCreditLimit(new CreditLimit(amount));
        } else {
            request.setAvailableWithdrawalLimit(new WithdrawalLimit(amount));
        }

        restTemplate.patchForObject("http://localhost:8080/v1/accounts/" + accountId, request, String.class);
    }

}
