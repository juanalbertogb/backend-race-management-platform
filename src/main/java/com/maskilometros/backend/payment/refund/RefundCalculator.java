package com.maskilometros.backend.payment.refund;

import com.maskilometros.backend.constants.ApplicationConstants;
import com.maskilometros.backend.dto.PaymentStatusEnum;
import com.maskilometros.backend.dto.RefundTypeEnum;
import com.maskilometros.backend.entity.Payment;
import com.maskilometros.backend.entity.Race;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class RefundCalculator {

    public RefundDecision calculate(Race race, Payment payment) {

        long daysUntilRace = ChronoUnit.DAYS.between(LocalDate.now(), race.getRaceDate().toLocalDate());

        if (daysUntilRace > 20) {
            return new RefundDecision(PaymentStatusEnum.REFUNDED, RefundTypeEnum.FULL, payment.getAmount(),
                    ApplicationConstants.FULL_REFUND_BEFORE_20_DAYS);
        }

        if (daysUntilRace >= 10) {
            BigDecimal refundAmount = payment.getAmount().multiply(new BigDecimal("0.50"))
                    .setScale(2, RoundingMode.HALF_UP);

            return new RefundDecision(PaymentStatusEnum.REFUNDED, RefundTypeEnum.PARTIAL, refundAmount,
                    ApplicationConstants.PARTIAL_REFUND_BEFORE_10_DAYS);
        }

        return new RefundDecision(PaymentStatusEnum.COMPLETED, RefundTypeEnum.NONE, BigDecimal.ZERO,
                    ApplicationConstants.NO_REFUND_TOO_LATE);
    }
}
