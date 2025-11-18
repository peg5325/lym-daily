package com.formom.daily.controller;

import com.formom.daily.dto.DailySummaryDto;
import com.formom.daily.service.DailySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 일일 요약 API 컨트롤러
 *
 * /api/today, /api/date/{date} 엔드포인트 제공
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DailySummaryController {

    private final DailySummaryService dailySummaryService;

    /**
     * 오늘의 요약 조회
     *
     * GET /api/today
     *
     * @return 오늘의 TOP 3 뉴스
     */
    @GetMapping("/today")
    public ResponseEntity<DailySummaryDto> getTodaySummary() {
        log.info("GET /api/today - Fetching today's summary");

        DailySummaryDto summary = dailySummaryService.getTodaySummary();

        return ResponseEntity.ok(summary);
    }

    /**
     * 특정 날짜의 요약 조회
     *
     * GET /api/date/2025-01-18
     *
     * @param date 조회할 날짜 (yyyy-MM-dd)
     * @return 해당 날짜의 TOP 3 뉴스
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<DailySummaryDto> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/date/{} - Fetching daily summary", date);

        DailySummaryDto summary = dailySummaryService.getDailySummary(date);

        return ResponseEntity.ok(summary);
    }
}
