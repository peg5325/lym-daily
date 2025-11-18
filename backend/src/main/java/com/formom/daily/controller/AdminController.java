package com.formom.daily.controller;

import com.formom.daily.agent.scheduler.SchedulerAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 API 컨트롤러
 *
 * 수동 뉴스 수집 등 관리 기능 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SchedulerAgent schedulerAgent;

    /**
     * 수동으로 뉴스 수집 실행
     *
     * POST /api/admin/collect
     *
     * @return 실행 결과
     */
    @PostMapping("/collect")
    public ResponseEntity<Map<String, String>> manualCollect() {
        log.info("POST /api/admin/collect - Manual news collection triggered");

        try {
            schedulerAgent.executeManualCollection();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "뉴스 수집이 성공적으로 완료되었습니다."
            ));

        } catch (Exception e) {
            log.error("Manual collection failed", e);

            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "뉴스 수집 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
