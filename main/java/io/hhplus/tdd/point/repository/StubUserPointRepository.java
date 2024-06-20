package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.dto.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StubUserPointRepository implements UserPointRepository{

    private final Map<Long, UserPoint> userPointTable = new HashMap<>();


    @Override
    public UserPoint selectById(long userId) {
        return userPointTable.getOrDefault(userId, UserPoint.empty(userId));
    }

    @Override
    public UserPoint insertOrUpdate(long userId, long point) {
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        userPointTable.put(userId, userPoint);
        return userPoint;
    }

    @Override
    public long getTotalUsers() {
        return userPointTable.size();
    }
}
