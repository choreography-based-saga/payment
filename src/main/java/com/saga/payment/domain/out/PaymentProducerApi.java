package com.saga.payment.domain.out;

import com.saga.payment.domain.model.Payment;

public interface PaymentProducerApi {

    void sendPayment(Payment payment);
}
