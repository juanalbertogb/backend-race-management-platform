package com.maskilometros.backend.entity;

import com.maskilometros.backend.dto.PaymentStatusEnum;
import com.maskilometros.backend.dto.RefundTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter
@Entity
@Table(name = "payments")
@NamedQueries({
        @NamedQuery(name = "Payment.findByIdWithRegistration", query =
                "SELECT p FROM Payment p JOIN FETCH p.registration r WHERE p.id=:paymentId"),
})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status = PaymentStatusEnum.PENDING;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false, length = 5)
    private String currency;

    @NotNull
    @Column(name = "idempotency_key", nullable = false, length = 50)
    private String idempotencyKey;

    @Column(name = "external_payment_id", length = 50)
    private String externalPaymentId;

    @Column(name = "provider_message", length = 100)
    private String providerMessage;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "refunded_amount")
    private BigDecimal refundedAmount;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refund_type")
    @Enumerated(EnumType.STRING)
    private RefundTypeEnum refundType;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "failed_at")
    private Instant failedAt;


}