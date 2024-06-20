package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.repository.UserPointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private UserPointHistoryRepository userPointHistoryRepository;

    public PointServiceImpl(UserPointRepository userPointRepository, UserPointHistoryRepository userPointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.userPointHistoryRepository = userPointHistoryRepository;
    }

    @Override
    public UserPoint registerUser(long id, long point) {
        UserPoint userPoint = userPointRepository.insertOrUpdate(id, point);
        userPointHistoryRepository.insert(id, point, TransactionType.CHARGE, System.currentTimeMillis());
        return userPoint;
    }

    @Override
    @Transactional(readOnly = true)
    public UserPoint getPointById(long id) {
        return userPointRepository.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointHistory> getPointHistories(long id) {
        return userPointHistoryRepository.getUserPointHistories(id);
    }

    @Override
    @Transactional
    public UserPoint chargePoint(long id, long amount) throws IllegalArgumentException {
        if(amount < 0) {
            throw new IllegalArgumentException("충전 포인트는 0보다 커야합니다.");
        }
        UserPoint userPoint = userPointRepository.selectById(id);
        long updatedAmount = userPoint.point() + amount;
        long time = System.currentTimeMillis();
        PointHistory history = userPointHistoryRepository.insert(id, amount, TransactionType.CHARGE, time);
        return userPointRepository.insertOrUpdate(id, updatedAmount);
    }

    @Override
    @Transactional
    public UserPoint usePoint(long id, long amount) throws InsufficientResourcesException {
        UserPoint userPoint = userPointRepository.selectById(id);
        long currentPoint = userPoint.point();
        if (currentPoint < amount) {
            throw new InsufficientResourcesException("잔액이 부족합니다");
        }
        long updatedAmount = currentPoint - amount;
        long time = System.currentTimeMillis();
        PointHistory history = userPointHistoryRepository.insert(id, amount, TransactionType.USE, time);
        return userPointRepository.insertOrUpdate(id, updatedAmount);
    }


}
