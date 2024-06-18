package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    @Autowired
    private final PointService pointService = new PointService();

    @GetMapping("{id}")
    public UserPoint getPoint(@PathVariable long id) {
        return pointService.getPointById(id);
    }

    @GetMapping("{id}/histories")
    public List<PointHistory> getPointHistories(@PathVariable long id) {
        return pointService.getPointHistories(id);
    }

    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id,@RequestBody long amount) {
        return pointService.chargePoint(id, amount);
    }

    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id, @RequestBody long amount) throws InsufficientResourcesException {
        return pointService.usePoint(id, amount);
    }
}
