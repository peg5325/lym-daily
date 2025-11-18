package com.formom.daily.agent.scheduler.impl;

import com.formom.daily.agent.ai.SummarizationAgent;
import com.formom.daily.agent.collector.NewsCollectorAgent;
import com.formom.daily.agent.curator.ContentCuratorAgent;
import com.formom.daily.agent.scheduler.SchedulerAgent;
import com.formom.daily.dto.NewsDto;
import com.formom.daily.entity.News;
import com.formom.daily.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 스케줄링 에이전트 구현체
 *
 * 매일 오전 7시에 자동으로 뉴스 수집 → AI 요약 → TOP 3 선정 → DB 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerAgentImpl implements SchedulerAgent {

    private final NewsCollectorAgent newsCollectorAgent;
    private final SummarizationAgent summarizationAgent;
    private final ContentCuratorAgent contentCuratorAgent;
    private final NewsRepository newsRepository;

    /**
     * 매일 오전 7시 자동 실행
     * Cron: "0 0 7 * * *" (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "${scheduler.cron}")
    @Override
    public void executeDailyNewsCollection() {
        log.info("===== Starting Daily News Collection =====");
        long startTime = System.currentTimeMillis();

        try {
            executeNewsCollectionFlow(LocalDate.now());

            long duration = System.currentTimeMillis() - startTime;
            log.info("===== Daily News Collection Completed Successfully in {}ms =====", duration);

        } catch (Exception e) {
            log.error("===== Daily News Collection Failed =====", e);
            // 실패해도 다음날 다시 시도하므로 예외를 던지지 않음
        }
    }

    @Override
    @Transactional
    public void executeManualCollection() {
        log.info("===== Starting Manual News Collection =====");
        executeNewsCollectionFlow(LocalDate.now());
    }

    /**
     * 뉴스 수집 플로우 실행
     * 1. NewsCollectorAgent: 뉴스 수집
     * 2. SummarizationAgent: AI 요약 및 중요도 점수 산정
     * 3. ContentCuratorAgent: TOP 3 선정
     * 4. DB 저장
     */
    @Transactional
    protected void executeNewsCollectionFlow(LocalDate date) {
        try {
            // Step 1: 뉴스 수집
            log.info("Step 1: Collecting news from Naver API");
            List<NewsDto> collectedNews = newsCollectorAgent.collectDailyNews(date);

            if (collectedNews.isEmpty()) {
                log.warn("No news collected for date: {}", date);
                return;
            }

            log.info("Collected {} news articles", collectedNews.size());

            // Step 2: AI 요약 및 중요도 점수 산정
            log.info("Step 2: Summarizing news with AI");
            List<NewsDto> summarizedNews = summarizationAgent.summarizeNewsBatch(collectedNews);
            log.info("Successfully summarized {} news articles", summarizedNews.size());

            // Step 3: TOP 3 선정
            log.info("Step 3: Selecting TOP 3 news");
            List<NewsDto> top3News = contentCuratorAgent.selectTodayTop3(summarizedNews);
            log.info("Selected TOP 3 news:");
            top3News.forEach(news ->
                    log.info("  - [Score: {}] {}", news.getImportanceScore(), news.getTitle())
            );

            // Step 4: 모든 뉴스 DB 저장 (TOP 3만이 아니라 전체 저장)
            log.info("Step 4: Saving all news to database");
            List<News> newsEntities = summarizedNews.stream()
                    .map(NewsDto::toEntity)
                    .collect(Collectors.toList());

            newsRepository.saveAll(newsEntities);
            log.info("Successfully saved {} news articles to database", newsEntities.size());

        } catch (Exception e) {
            log.error("Failed to execute news collection flow", e);
            throw new RuntimeException("News collection flow failed", e);
        }
    }
}
