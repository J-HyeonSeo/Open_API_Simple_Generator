package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Page<Payment> findByMember(Member member, Pageable pageable);
}
