package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.dto.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository{

    private final Map<Long, UserPoint> userPointTable = new HashMap<>();


    @Override
    public UserPoint selectById(long id) {
        return userPointTable.getOrDefault(id, UserPoint.empty(id));
    }

    @Override
    public UserPoint insertUserPoint(long id, long point, long updateMillis) {
        return userPointTable.put(id, new UserPoint(id, point, updateMillis));
    }
}
