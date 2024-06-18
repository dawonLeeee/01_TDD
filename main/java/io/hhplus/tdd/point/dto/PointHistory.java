package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public record PointHistory(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
