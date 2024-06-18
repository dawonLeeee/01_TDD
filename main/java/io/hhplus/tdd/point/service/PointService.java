package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.List;

@Service
public class PointService {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    public UserPoint getPointById(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistories(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        long updatedAmount = userPoint.point() + amount;
        PointHistory history = pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, updatedAmount);
    }

    public UserPoint usePoint(long id, long amount) throws InsufficientResourcesException {
        UserPoint userPoint = userPointTable.selectById(id);
        long currentPoint = userPoint.point();
        if (currentPoint < amount) {
            throw new InsufficientResourcesException("잔액이 부족합니다");
        }
        long updatedAmount = currentPoint - amount;
        PointHistory history = pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, updatedAmount);
    }
}
