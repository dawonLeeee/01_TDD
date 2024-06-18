package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> getUserPointHistory(Long id) {
        return this.pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public PointHistory insertPointHistory(long id, long amount, TransactionType type, long updateMillis) {
        return this.pointHistoryTable.insert(id, amount, type, updateMillis);
    }

}
