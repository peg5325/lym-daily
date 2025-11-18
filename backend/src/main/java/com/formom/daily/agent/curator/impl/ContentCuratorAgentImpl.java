package com.formom.daily.agent.curator.impl;

import com.formom.daily.agent.curator.ContentCuratorAgent;
import com.formom.daily.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 콘텐츠 큐레이션 에이전트 구현체
 *
 * 중요도, 출처 신뢰도, 최신성을 종합하여 TOP 3 뉴스 선정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContentCuratorAgentImpl implements ContentCuratorAgent {

    // 신뢰할 수 있는 출처 목록 (가중치 높음)
    private static final Map<String, Double> TRUSTED_SOURCES = Map.of(
            "연합뉴스", 1.5,
            "KBS", 1.4,
            "MBC", 1.4,
            "SBS", 1.4,
            "조선일보", 1.3,
            "중앙일보", 1.3,
            "동아일보", 1.3,
            "한국일보", 1.3,
            "네이버뉴스", 1.2
    );

    @Override
    public List<NewsDto> selectTopNews(List<NewsDto> newsList, int topN) {
        log.info("Selecting top {} news from {} items", topN, newsList.size());

        if (newsList == null || newsList.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 중복 제거
        List<NewsDto> uniqueNews = removeDuplicates(newsList);

        // 2. 랭킹 점수 계산 및 정렬
        List<NewsDto> rankedNews = uniqueNews.stream()
                .sorted((n1, n2) -> {
                    Double score1 = calculateRankingScore(n1);
                    Double score2 = calculateRankingScore(n2);
                    return score2.compareTo(score1); // 내림차순
                })
                .limit(topN)
                .collect(Collectors.toList());

        log.info("Selected top {} news. Scores: {}", topN,
                rankedNews.stream()
                        .map(news -> String.format("%s: %.2f", news.getTitle(), calculateRankingScore(news)))
                        .collect(Collectors.joining(", ")));

        return rankedNews;
    }

    @Override
    public List<NewsDto> selectTodayTop3(List<NewsDto> newsList) {
        return selectTopNews(newsList, 3);
    }

    @Override
    public List<NewsDto> removeDuplicates(List<NewsDto> newsList) {
        log.info("Removing duplicates from {} news items", newsList.size());

        // URL 기반 중복 제거
        Set<String> seenUrls = new HashSet<>();
        List<NewsDto> uniqueNews = new ArrayList<>();

        for (NewsDto news : newsList) {
            if (news.getUrl() != null && !seenUrls.contains(news.getUrl())) {
                seenUrls.add(news.getUrl());
                uniqueNews.add(news);
            }
        }

        // 제목 유사도 기반 중복 제거 (간단한 버전)
        List<NewsDto> finalUnique = new ArrayList<>();
        Set<String> seenTitles = new HashSet<>();

        for (NewsDto news : uniqueNews) {
            String normalizedTitle = normalizeTitle(news.getTitle());
            if (!seenTitles.contains(normalizedTitle)) {
                seenTitles.add(normalizedTitle);
                finalUnique.add(news);
            }
        }

        log.info("Removed {} duplicates. Remaining: {}", newsList.size() - finalUnique.size(), finalUnique.size());

        return finalUnique;
    }

    @Override
    public Double calculateRankingScore(NewsDto news) {
        if (news == null) {
            return 0.0;
        }

        // 1. 중요도 점수 (0-100) - 가중치 70%
        double importanceScore = (news.getImportanceScore() != null ? news.getImportanceScore() : 50) * 0.7;

        // 2. 출처 신뢰도 - 가중치 20%
        double sourceReliability = getSourceReliability(news.getSource()) * 20.0;

        // 3. 최신성 - 가중치 10%
        double recencyScore = getRecencyScore(news.getPublishedAt()) * 10.0;

        double totalScore = importanceScore + sourceReliability + recencyScore;

        log.debug("Ranking score for '{}': importance={}, source={}, recency={}, total={}",
                news.getTitle(), importanceScore, sourceReliability, recencyScore, totalScore);

        return totalScore;
    }

    /**
     * 출처 신뢰도 가져오기
     */
    private Double getSourceReliability(String source) {
        if (source == null) {
            return 1.0;
        }

        // 신뢰할 수 있는 출처인지 확인
        for (Map.Entry<String, Double> entry : TRUSTED_SOURCES.entrySet()) {
            if (source.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return 1.0; // 기본값
    }

    /**
     * 최신성 점수 계산
     * (오늘 날짜에 가까울수록 높은 점수)
     */
    private Double getRecencyScore(LocalDate publishedAt) {
        if (publishedAt == null) {
            return 0.5;
        }

        LocalDate today = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(publishedAt, today);

        // 오늘: 1.0, 1일 전: 0.9, 2일 전: 0.8, ...
        return Math.max(0.0, 1.0 - (daysDiff * 0.1));
    }

    /**
     * 제목 정규화 (중복 판단용)
     */
    private String normalizeTitle(String title) {
        if (title == null) {
            return "";
        }

        // 특수문자, 공백 제거하고 소문자로 변환
        return title.replaceAll("[^가-힣a-zA-Z0-9]", "")
                .toLowerCase()
                .trim();
    }
}
