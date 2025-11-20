package com.formom.daily.controller;

import com.formom.daily.dto.ScheduleDto;
import com.formom.daily.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 스케줄 API 컨트롤러
 *
 * 스케줄 CRUD 엔드포인트 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 스케줄 생성
     *
     * POST /api/schedules
     *
     * @param scheduleDto 생성할 스케줄 정보
     * @return 생성된 스케줄
     */
    @PostMapping
    public ResponseEntity<ScheduleDto> createSchedule(@Valid @RequestBody ScheduleDto scheduleDto) {
        log.info("POST /api/schedules - Creating new schedule");

        ScheduleDto created = scheduleService.createSchedule(scheduleDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 이번 주 스케줄 조회
     *
     * GET /api/schedules/week
     *
     * @return 이번 주 스케줄 목록
     */
    @GetMapping("/week")
    public ResponseEntity<List<ScheduleDto>> getWeekSchedules() {
        log.info("GET /api/schedules/week - Fetching week schedules");

        List<ScheduleDto> schedules = scheduleService.getWeekSchedules();

        return ResponseEntity.ok(schedules);
    }

    /**
     * 특정 기간 스케줄 조회
     *
     * GET /api/schedules?startDate=2025-01-01&endDate=2025-01-31
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 스케줄 목록
     */
    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getSchedulesByPeriod(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            log.info("GET /api/schedules - Fetching schedules from {} to {}", startDate, endDate);
            List<ScheduleDto> schedules = scheduleService.getSchedulesByPeriod(startDate, endDate);
            return ResponseEntity.ok(schedules);
        } else {
            log.info("GET /api/schedules - Fetching all schedules");
            List<ScheduleDto> schedules = scheduleService.getAllSchedules();
            return ResponseEntity.ok(schedules);
        }
    }

    /**
     * 스케줄 단건 조회
     *
     * GET /api/schedules/{id}
     *
     * @param id 스케줄 ID
     * @return 스케줄 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable Long id) {
        log.info("GET /api/schedules/{} - Fetching schedule", id);

        ScheduleDto schedule = scheduleService.getSchedule(id);

        return ResponseEntity.ok(schedule);
    }

    /**
     * 스케줄 수정
     *
     * PUT /api/schedules/{id}
     *
     * @param id 스케줄 ID
     * @param scheduleDto 수정할 스케줄 정보
     * @return 수정된 스케줄
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDto> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleDto scheduleDto) {
        log.info("PUT /api/schedules/{} - Updating schedule", id);

        ScheduleDto updated = scheduleService.updateSchedule(id, scheduleDto);

        return ResponseEntity.ok(updated);
    }

    /**
     * 스케줄 삭제
     *
     * DELETE /api/schedules/{id}
     *
     * @param id 스케줄 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSchedule(@PathVariable Long id) {
        log.info("DELETE /api/schedules/{} - Deleting schedule", id);

        scheduleService.deleteSchedule(id);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "스케줄이 삭제되었습니다."
        ));
    }

    /**
     * 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", "error",
                "message", e.getMessage()
        ));
    }
}
