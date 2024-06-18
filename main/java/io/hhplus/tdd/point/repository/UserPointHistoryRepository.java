package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;

import java.util.List;

public interface UserPointHistoryRepository {
    public List<PointHistory> getUserPointHistory(Long id);
    public PointHistory insertPointHistory(long id, long amount, TransactionType type, long updateMillis);
}
