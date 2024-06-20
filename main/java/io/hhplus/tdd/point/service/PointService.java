package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.List;

@Service
public interface PointService {
    public UserPoint getPointById(long id);

    public List<PointHistory> getPointHistories(long id);

    public UserPoint chargePoint(long id, long amount) throws IllegalArgumentException;

    public UserPoint usePoint(long id, long amount) throws InsufficientResourcesException;

    UserPoint registerUser(long id, long point);
}
