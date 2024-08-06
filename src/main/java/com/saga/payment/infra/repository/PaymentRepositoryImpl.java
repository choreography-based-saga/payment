package com.saga.payment.infra.repository;

import com.saga.payment.domain.model.Payment;
import com.saga.payment.domain.out.PaymentRepositoryApi;
import com.saga.payment.infra.mapper.PaymentEntityMapper;
import com.saga.payment.infra.model.PaymentEntity;
import com.saga.payment.infra.model.enums.PaymentTransactionStatus;
import com.saga.payment.infra.repository.jpa.PaymentEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PaymentRepositoryImpl implements PaymentRepositoryApi {
    private final PaymentEntityRepository paymentEntityRepository;
    private final PaymentEntityMapper mapper;

    @Override
    public List<Payment> findAll() {
        return mapper.toDomain(paymentEntityRepository.findAll());
    }

    @Override
    public Payment createPayment(String orderId, BigDecimal amount, UUID customerId) {
        PaymentEntity payment = PaymentEntity.builder()
                .status(PaymentTransactionStatus.CREATED)
                .transactionAmount(amount)
                .transactionId(UUID.randomUUID())
                .customerId(customerId)
                .orderId(orderId)
                .build();
        return mapper.toDomain(paymentEntityRepository.save(payment));
    }

    @Override
    public List<Payment> createBankTransaction(List<Payment> payments) {
        List<PaymentEntity> paymentEntities = mapper.toEntity(payments);
        for (PaymentEntity payment : paymentEntities) {
            payment.setBankTransactionId(UUID.randomUUID().toString());
            payment.setStatus(PaymentTransactionStatus.COMPLETED);
            payment.setPaidAmount(payment.getTransactionAmount());
            log.info("Sending bank transaction request for payment {}", payment.getId());
        }
        return mapper.toDomain(paymentEntityRepository.saveAll(paymentEntities));
    }

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(paymentEntityRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentEntityRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentEntityRepository.findByOrderId(orderId).map(mapper::toDomain);
    }
}
