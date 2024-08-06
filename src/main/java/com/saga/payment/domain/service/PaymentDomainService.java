package com.saga.payment.domain.service;

import com.saga.payment.domain.in.PaymentDomainServiceApi;
import com.saga.payment.domain.model.Claim;
import com.saga.payment.domain.model.Order;
import com.saga.payment.domain.model.Payment;
import com.saga.payment.domain.model.enums.ClaimStatus;
import com.saga.payment.domain.model.enums.OrderDomainStatus;
import com.saga.payment.domain.model.enums.TransactionStatus;
import com.saga.payment.domain.out.PaymentProducerApi;
import com.saga.payment.domain.out.PaymentRepositoryApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class PaymentDomainService implements PaymentDomainServiceApi {
    private final PaymentRepositoryApi paymentRepositoryApi;
    private final PaymentProducerApi paymentProducerApi;

    @Override
    public List<Payment> getAll() {
        return paymentRepositoryApi.findAll();
    }

    @Override
    public void processClaim(Claim claim) {
        if (!claim.status().equals(ClaimStatus.REFUNDED)) {
            return;
        }
        Payment payment = paymentRepositoryApi.createPayment(claim.orderId(), claim.refundAmount(), claim.customerId());
        paymentProducerApi.send(payment);
    }

    @Override
    public void initiateBankTransaction() {
        List<Payment> payments = paymentRepositoryApi.findAll()
                .stream()
                .filter(p -> !p.status().equals(TransactionStatus.CANCELLED) &&
                        !p.status().equals(TransactionStatus.COMPLETED))
                .toList();

        payments = paymentRepositoryApi.createBankTransaction(payments);
        payments.forEach(paymentProducerApi::send);
    }

    @Override
    public boolean cancelPayment(UUID paymentId) {
        Optional<Payment> maybePayment = paymentRepositoryApi.findById(paymentId);
        if (maybePayment.isEmpty()) {
            return false;
        }
        Payment payment = maybePayment.get();
        payment = payment.updateStatus(TransactionStatus.CANCELLED);
        payment = paymentRepositoryApi.save(payment);
        paymentProducerApi.send(payment);
        return true;
    }

    @Override
    public void processOrder(Order order) {
        Optional<Payment> maybePayment = paymentRepositoryApi.findByOrderId(order.orderId());
        if (order.status().equals(OrderDomainStatus.PENDING) && maybePayment.isEmpty()) {
            Payment payment = paymentRepositoryApi.save(new Payment(
                    UUID.randomUUID(),
                    order.grandTotal(),
                    order.grandTotal(),
                    TransactionStatus.CREATED,
                    UUID.randomUUID(),
                    order.orderId(),
                    null,
                    order.customerId()));
            List<Payment> payments = paymentRepositoryApi.createBankTransaction(List.of(payment));
            payments.forEach(paymentProducerApi::send);

        }
    }
}
