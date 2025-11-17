package com.formom.daily.controller;

import com.formom.daily.agent.collector.NewsCollectorAgent;
import com.formom.daily.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 뉴스 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsCollectorAgent newsCollectorAgent;

    /**
     * 오늘의 뉴스 수집 (테스트용)
     */
    @GetMapping("/collect")
    public ResponseEntity<List<NewsDto>> collectNews() {
        log.info("Manual news collection triggered");

        try {
            List<NewsDto> newsList = newsCollectorAgent.collectDailyNews(LocalDate.now());
            return ResponseEntity.ok(newsList);
        } catch (Exception e) {
            log.error("Failed to collect news: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 키워드로 뉴스 검색 (테스트용)
     */
    @GetMapping("/search")
    public ResponseEntity<List<NewsDto>> searchNews(
            @RequestParam(defaultValue = "임영웅") String keyword,
            @RequestParam(defaultValue = "10") int count) {

        log.info("News search triggered - keyword: {}, count: {}", keyword, count);

        try {
            List<NewsDto> newsList = newsCollectorAgent.searchNews(keyword, count);
            return ResponseEntity.ok(newsList);
        } catch (Exception e) {
            log.error("Failed to search news: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
