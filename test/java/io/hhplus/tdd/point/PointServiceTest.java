package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.repository.StubUserPointHistoryRepository;
import io.hhplus.tdd.point.repository.StubUserPointRepository;
import io.hhplus.tdd.point.repository.UserPointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.naming.InsufficientResourcesException;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointServiceTest {

    private PointService pointService;
    private UserPointRepository userPointRepository;
    private UserPointHistoryRepository userPointHistoryRepository;

    @BeforeEach
    void setUp() {
        userPointRepository = new StubUserPointRepository();
        userPointHistoryRepository = new StubUserPointHistoryRepository();
        pointService = new PointServiceImpl(userPointRepository, userPointHistoryRepository);

        // 기본 사용자 등록 및 충전
        pointService.registerUser(0L, 10000L);
    }

    @Test
    @DisplayName("사용자 등록 성공")
    void 사용자_등록() throws Exception {
        // Given
        long userId = 1L;
        long basicPoint = 5000L;

        // When
        UserPoint userPoint = pointService.registerUser(userId, basicPoint);

        // Then
        assertEquals(userPointRepository.getTotalUsers(), 2);
        assertEquals(userPointRepository.selectById(userId).point(), basicPoint);
    }

    @Test
    @DisplayName("중복_사용자_등록")
    void 중복_사용자_등록() throws Exception {
        // Given
        long userId = 0L;
        long newPoint = 5000L;

        // When
        UserPoint updatedUserPoint = pointService.registerUser(userId, newPoint);

        // Then
        assertEquals(updatedUserPoint.point(), newPoint);
        assertEquals(userPointRepository.selectById(userId).point(), newPoint); // 중복 등록 시 포인트 업데이트
        assertEquals(userPointRepository.getTotalUsers(), 1);
    }

    @Test
    @DisplayName("포인트 조회 성공")
    void 포인트_조회() {
        // Given
        long userId = 0L;

        // When
        UserPoint userPoint = pointService.getPointById(userId);

        // Then
        assertEquals(userPoint.point(), 10000L);
    }

    @Test
    @DisplayName("새로운 사용자의 포인트 조회시: 0")
    void 새_사용자_포인트_조회() {
        // Given
        long userId = 99L;

        // When
        UserPoint userPoint = pointService.getPointById(userId);

        // Then
        assertEquals(userPoint.point(), 0);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void 포인트_충전_성공() {
        // given
        long userId = 1L;
        long chargeAmount = 1000L;

        // when
        UserPoint userPoint = pointService.chargePoint(userId, chargeAmount);

        // then
        assertEquals(userPoint.point(), chargeAmount);
        assertEquals(userPointHistoryRepository.getUserPointHistories(userId).size(), 1);
    }

    @Test
    @DisplayName("음수 포인트 충전 실패")
    void 포인트_사용_실패() {
        // given
        long userId = 1L;
        long chargeAmount = -1000L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(userId, chargeAmount));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void 포인트_사용_성공() throws InsufficientResourcesException {
        // given
        long userId = 0L;
        long useAmount = 1000L;

        // when
        UserPoint userPoint = pointService.usePoint(userId, useAmount);

        // then
        assertEquals(userPoint.point(), 10000L - useAmount);
        assertEquals(userPointHistoryRepository.getUserPointHistories(userId).size(), 2); // init, 사용1회
    }

    @Test
    @DisplayName("포인트 사용 실패")
    void 포인트_사용_실패_잔액부족() throws InsufficientResourcesException {
        // given
        long userId = 0L;
        long useAmount = 10001L;

        // when & then
        assertThrows(InsufficientResourcesException.class, () -> pointService.usePoint(userId, useAmount));
    }

    @Test
    @DisplayName("포인트 사용 내역 조회 성공")
    void 포인트_사용_내역() throws InsufficientResourcesException {
        // given
        long userId = 1L;
        UserPoint charge1 = pointService.chargePoint(userId, 1000L);
        UserPoint charge2 = pointService.chargePoint(userId, 5000L);
        UserPoint use2 = pointService.usePoint(userId, 1500L);

        // when
        List<PointHistory> histories = pointService.getPointHistories(userId);

        // then
        assertEquals(histories.size(), 3);
        assertEquals(histories.get(0).amount(), 1000L);
        assertEquals(histories.get(0).type(), TransactionType.CHARGE);
        assertEquals(histories.get(2).amount(), 1500L);
        assertEquals(histories.get(2).type(), TransactionType.USE);
        assertEquals(use2.point(), 1000L + 5000L - 1500L);
    }
}