package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.dto.UserPoint;

public interface UserPointRepository {

    UserPoint selectById(long id);
    UserPoint insertUserPoint(long id, long point, long updateMillis);
}
