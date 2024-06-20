package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class StubUserPointHistoryRepository implements UserPointHistoryRepository {

    private List<PointHistory> histories = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong();


    @Override
    public List<PointHistory> getUserPointHistories(Long id) {
        List<PointHistory> userHistories = new ArrayList<>();
        for(PointHistory history : histories) {
            if (history.userId() == id) {
                userHistories.add(history);
            }
        }
        return userHistories;
    }

    @Override
    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        long id = idGenerator.incrementAndGet();
        PointHistory history = new PointHistory(id, userId, amount, type, updateMillis);
        histories.add(history);
        return history;
    }
}
