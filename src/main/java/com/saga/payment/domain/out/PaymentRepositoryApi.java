package com.saga.payment.domain.out;

import com.saga.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryApi {

    List<Payment> findAll();

    Payment createPayment(String orderId, BigDecimal amount, UUID customerId);

    List<Payment> createBankTransaction(List<Payment> payments);

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    Optional<Payment> findByOrderId(String orderId);
}
