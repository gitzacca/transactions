package br.com.pismo.transactions.external;

import br.com.pismo.transactions.domain.AccountLimitBalancer;
import br.com.pismo.transactions.domain.LimitType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class HttpAccountLimitBalancer implements AccountLimitBalancer {

//    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void updateLimits(Long accountId, LimitType limitType, BigDecimal amount) {
        Request request = new Request();

        if (limitType.equals(LimitType.CREDIT)) {
            request.setAvailableCreditLimit(new CreditLimit(amount));
        } else {
            request.setAvailableWithdrawalLimit(new WithdrawalLimit(amount));
        }

//        HttpEntity<Request> requestHttpEntity = new HttpEntity<>(request);
//        restTemplate.patchForObject("http://localhost:8081/v1/accounts/" + accountId, requestHttpEntity, String.class);


        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "merge-patch+json");
        headers.setContentType(mediaType);

        HttpEntity<Request> entity = new HttpEntity<>(request, headers);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        restTemplate.exchange("http://localhost:8081/v1/accounts/" + accountId, HttpMethod.PATCH, entity, Void.class);
    }

}
