package com.maskilometros.backend.payment.refund;

import com.maskilometros.backend.dto.PaymentStatusEnum;
import com.maskilometros.backend.dto.RefundTypeEnum;

import java.math.BigDecimal;

public record RefundDecision(
        PaymentStatusEnum status,
        RefundTypeEnum refundType,
        BigDecimal refundedAmount,
        String reason) {


}
