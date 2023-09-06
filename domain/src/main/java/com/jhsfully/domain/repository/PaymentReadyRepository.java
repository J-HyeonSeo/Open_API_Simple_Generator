package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.PaymentReady;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentReadyRepository extends CrudRepository<PaymentReady, String> {

}
