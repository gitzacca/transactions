package br.com.pismo.transactions.external;

import br.com.pismo.transactions.domain.AccountLimitBalancer;
import br.com.pismo.transactions.domain.LimitType;
import br.com.pismo.transactions.external.exceptions.AccountNotFoundException;
import br.com.pismo.transactions.external.exceptions.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class HttpAccountLimitBalancer implements AccountLimitBalancer {

    private HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    private RestTemplate restTemplate = new RestTemplate(requestFactory);

    @Value("${app.accounts.url}")
    private String accountsUrl;

    @Override
    public void updateLimits(Long accountId, LimitType limitType, BigDecimal amount) {
        Request request = new Request();

        if (limitType.equals(LimitType.CREDIT)) {
            request.setAvailableCreditLimit(new CreditLimit(amount));
        } else {
            request.setAvailableWithdrawalLimit(new WithdrawalLimit(amount));
        }

        sendRequest(request, accountId);
    }

    private void sendRequest(Request request, Long accountId) {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "merge-patch+json");
        headers.setContentType(mediaType);

        HttpEntity<Request> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(accountsUrl + accountId, HttpMethod.PATCH, entity, Void.class);
        } catch (HttpClientErrorException exception) {
            HttpStatus statusCode = exception.getStatusCode();

            if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
                throw new InsufficientFundsException("Insufficient funds");
            } else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
                throw new AccountNotFoundException("Account with id: " + accountId + " not found");
            } else {
                throw new RuntimeException("Unknow server error. Contact support.");
            }
        }
    }

}
