package com.saga.payment.application.api.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saga.payment.application.api.enums.ClaimEventStatus;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClaimMessage(
        Integer id,
        Integer itemId,
        String orderId,
        BigDecimal refundAmount,
        ClaimEventStatus status,
        UUID customerId
) {
}
