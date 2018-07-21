package br.com.pismo.transactions.domain;

import org.springframework.data.repository.CrudRepository;

public interface PaymentTrackingRepository extends CrudRepository<PaymentTracking, Long> {
}
