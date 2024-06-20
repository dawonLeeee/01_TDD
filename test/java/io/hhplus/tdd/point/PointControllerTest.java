package io.hhplus.tdd.point;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointService pointService;

    @BeforeEach
    void setUp() {
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
        pointService.registerUser(userId, basicPoint);

        // Then
        mockMvc.perform(post("/point/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + userId + ",\"point\":" + basicPoint + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.point", is((int) basicPoint)));

        assertEquals(pointService.getPointById(userId).point(), basicPoint);
    }

//    @Test
//    @DisplayName("중복_사용자_등록")
//    void 중복_사용자_등록() throws Exception {
//        // Given
//        long userId = 0L;
//        long newPoint = 5000L;
//
//        // When
//        UserPoint userPoint = pointService.registerUser(userId, newPoint);
//
//        // Then
//        assertEquals(updatedUserPoint.point(), newPoint);
//        assertEquals(userPointTable.selectById(userId).point(), newPoint); // 중복 등록 시 포인트 업데이트
//        assertEquals(userPointTable.getTotalUsers(), 1);
//    }

    @Test
    @DisplayName("포인트 조회 성공")
    void 포인트_조회() throws Exception {
        // Given
        long userId = 999L;
        long basicPoint = 10000L;

        // When & Then
        mockMvc.perform(get("/point/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)userId)))
                .andExpect(jsonPath("$.point", is((int)basicPoint)));
    }

    @Test
    @DisplayName("포인트 조회 성공_ 새로운 사용자는 0원")
    void 포인트_조회_새로운_사용자() throws Exception {
        // Given
        long userId = 999L;

        // When & Then
        mockMvc.perform(get("/point/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)userId)))
                .andExpect(jsonPath("$.point", is((int)0)));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void 포인트_사용() throws Exception {
        // given
        long userId = 0L;
        long useAmount = 10001L;

        // When & Then
        mockMvc.perform(patch("/point/" + userId + "/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(useAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)userId)))
                .andExpect(jsonPath("$.point", is((int)(10000L - useAmount))));
    }

    @Test
    @DisplayName("포인트 사용 실패")
    void 포인트_사용_실패_잔액부족() throws Exception {

        // Given
        long userId = 0L;
        long useAmount = 1000L;

        // When & Then
        mockMvc.perform(patch("/point/" + userId + "/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(useAmount)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("잔액이 부족합니다"));
    }

    @Test
    @DisplayName("포인트 사용 내역 조회")
    void 포인트_사용_내역_조회() throws Exception {
        // Given
        long userId = 1L;
        long chargeAmount = 10000L;
        long useAmount = 5000L;

        // Charge some points
        mockMvc.perform(patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().isOk());

        // When & Then
        mockMvc.perform(patch("/point/" + userId + "/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(useAmount)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/point/" + userId + "/histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Expecting 2 transactions: charge and use
                .andExpect(jsonPath("$.[0].amount").value(chargeAmount))
                .andExpect(jsonPath("$.[0].type").value("CHARGE"))
                .andExpect(jsonPath("$.[1].amount").value(useAmount))
                .andExpect(jsonPath("$.[1].type").value("USE"));
    }
    @Test
    @DisplayName("포인트 충전 성공")
    void 포인트_충전_성공() throws Exception {
        // Given
        long userId = 0L;
        long chargeAmount = 1000L;

        // When & Then
        mockMvc.perform(patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(11000L)); // Expecting updated point value
    }

    @Test
    @DisplayName("포인트 충전 실패 - 음수 충전 불가")
    void 포인트_충전_실패_음수충전불가() throws Exception {
        // Given
        long userId = 0L;
        long chargeAmount = -1000L;

        // When & Then
        mockMvc.perform(patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("충전 포인트는 0보다 커야합니다."));
    }

}
