package com.saga.payment.application.messaging.producer;

import com.saga.payment.domain.model.Payment;
import com.saga.payment.domain.out.PaymentProducerApi;
import com.saga.payment.infra.common.event.StreamBindingsConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer implements PaymentProducerApi {

    private final StreamBridge streamBridge;

    public void sendPayment(Payment payment) {
        streamBridge.send(StreamBindingsConstants.PAYMENT,
                MessageBuilder.withPayload(payment).build());
    }
}
