package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import javax.naming.InsufficientResourcesException;
import static org.junit.jupiter.api.Assertions.*;

class PointControllerTest {

    @Autowired
    private PointController pointController;

    @Autowired
    private UserPointTable userPointTable;


    @BeforeEach
    void setUp() {
        pointController = new PointController();
        userPointTable = new UserPointTable();

    }

    @Test
    @DisplayName("사용자 등록 성공")
    void 사용자_등록() throws Exception {
        // Given
        long userId = 1L;
        long basicPoint = 5000L;

        // When
        UserPoint userPoint = userPointTable.insertOrUpdate(userId, basicPoint);

        // Then
        assertEquals(userPoint.point(), basicPoint);
        assertEquals(userPointTable.getTotalUsers(), 1);
    }

    @Test
    @DisplayName("포인트 조회 성공")
    void 포인트_조회() {
        // Given
        long userId = 1L;
        long basicPoint = 5000L;
        userPointTable.insertOrUpdate(userId, basicPoint);

        // When
        UserPoint userPoint = userPointTable.selectById(userId);

        // Then
        assertEquals(userPoint.point(), basicPoint);
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void 포인트_사용() throws InsufficientResourcesException {
        // Given
        long userId = 1L;
        long basicPoint = 5000L;
        long useAmount = 1000L;
        userPointTable.insertOrUpdate(userId, basicPoint);

        // When
        UserPoint userPoint = pointController.use(userId, useAmount);

        // Then
        assertEquals(userPoint.point(), basicPoint - useAmount);
    }

    @Test
    @DisplayName("포인트 사용 실패")
    void 포인트_사용_실패_잔액부족() throws InsufficientResourcesException {
//        PointController pointController = new PointController(userPointTable, pointHistoryTable, pointService);
//        // given
//        long userId = 0L;
//        long useAmount = 10001L;
//
//        // when
//        UserPoint userPoint = pointController.use(userId, useAmount);
//
//        // then
//        assertThrows(InsufficientResourcesException.class, () -> pointController.use(userId, useAmount));
    }



    @Test
    void history() {
    }

    @Test
    void charge() {
    }


}