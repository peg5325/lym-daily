package com.formom.daily.service;

import com.formom.daily.agent.curator.ContentCuratorAgent;
import com.formom.daily.dto.DailySummaryDto;
import com.formom.daily.dto.NewsDto;
import com.formom.daily.entity.News;
import com.formom.daily.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 일일 요약 서비스
 *
 * /api/today, /api/date/{date} 엔드포인트에 대한 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailySummaryService {

    private final NewsRepository newsRepository;
    private final ContentCuratorAgent contentCuratorAgent;

    /**
     * 오늘의 요약 조회
     */
    @Transactional(readOnly = true)
    public DailySummaryDto getTodaySummary() {
        return getDailySummary(LocalDate.now());
    }

    /**
     * 특정 날짜의 요약 조회
     */
    @Transactional(readOnly = true)
    public DailySummaryDto getDailySummary(LocalDate date) {
        log.info("Getting daily summary for date: {}", date);

        // 해당 날짜의 모든 뉴스 조회
        List<News> newsEntities = newsRepository.findByPublishedAtOrderByImportanceScoreDesc(date);

        if (newsEntities.isEmpty()) {
            log.warn("No news found for date: {}", date);
            return DailySummaryDto.builder()
                    .date(date)
                    .formattedDate(formatDate(date))
                    .topNews(List.of())
                    .totalNewsCount(0)
                    .build();
        }

        // Entity -> DTO 변환
        List<NewsDto> newsList = newsEntities.stream()
                .map(NewsDto::fromEntity)
                .collect(Collectors.toList());

        // TOP 3 선정
        List<NewsDto> top3News = contentCuratorAgent.selectTodayTop3(newsList);

        log.info("Found {} news for date: {}, selected TOP 3", newsEntities.size(), date);

        return DailySummaryDto.builder()
                .date(date)
                .formattedDate(formatDate(date))
                .topNews(top3News)
                .totalNewsCount(newsEntities.size())
                .build();
    }

    /**
     * 날짜를 한글 형식으로 포맷
     * "2025년 1월 18일 토요일"
     */
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN);
        return date.format(formatter);
    }
}
