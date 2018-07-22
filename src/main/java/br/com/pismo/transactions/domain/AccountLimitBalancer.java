package br.com.pismo.transactions.domain;

import java.math.BigDecimal;

public interface AccountLimitBalancer {

    void updateLimits(Long accountId, LimitType limitType, BigDecimal amount);

}
