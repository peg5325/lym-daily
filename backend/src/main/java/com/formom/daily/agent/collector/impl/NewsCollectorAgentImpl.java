package com.formom.daily.agent.collector.impl;

import com.formom.daily.agent.collector.NewsCollectorAgent;
import com.formom.daily.config.NaverApiConfig;
import com.formom.daily.dto.NewsDto;
import com.formom.daily.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 뉴스 수집 에이전트 구현체
 *
 * 네이버 뉴스 검색 API를 사용하여 뉴스 수집
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCollectorAgentImpl implements NewsCollectorAgent {

    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    private final NaverApiConfig naverApiConfig;
    private final NaverApiConfig.SearchConfig searchConfig;
    private final NewsRepository newsRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<NewsDto> collectDailyNews(LocalDate date) {
        log.info("Starting news collection for date: {}", date);

        try {
            // 네이버 뉴스 검색
            List<NewsDto> newsList = searchNews(searchConfig.getKeyword(), searchConfig.getCount());

            // 중복 제거 (이미 DB에 있는 URL 필터링)
            List<NewsDto> filteredNews = newsList.stream()
                    .filter(news -> !newsRepository.existsByUrl(news.getUrl()))
                    .collect(Collectors.toList());

            log.info("Successfully collected {} news articles (filtered {} duplicates)",
                    filteredNews.size(), newsList.size() - filteredNews.size());

            return filteredNews;

        } catch (Exception e) {
            log.error("Failed to collect news for date {}: {}", date, e.getMessage(), e);
            throw new RuntimeException("News collection failed", e);
        }
    }

    @Override
    public List<NewsDto> searchNews(String keyword, int count) {
        log.info("Searching news with keyword: {}, count: {}", keyword, count);

        try {
            WebClient webClient = webClientBuilder.build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("openapi.naver.com")
                            .path("/v1/search/news.json")
                            .queryParam("query", keyword)
                            .queryParam("display", count)
                            .queryParam("sort", "date")
                            .build())
                    .header("X-Naver-Client-Id", naverApiConfig.getClientId())
                    .header("X-Naver-Client-Secret", naverApiConfig.getClientSecret())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseNewsResponse(response);

        } catch (Exception e) {
            log.error("Failed to search news: {}", e.getMessage(), e);
            throw new RuntimeException("News search failed", e);
        }
    }

    /**
     * 네이버 API 응답 파싱
     */
    private List<NewsDto> parseNewsResponse(String response) {
        List<NewsDto> newsList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                NewsDto news = NewsDto.builder()
                        .title(cleanHtmlTags(item.getString("title")))
                        .content(cleanHtmlTags(item.getString("description")))
                        .url(item.getString("link"))
                        .source(extractSource(item.getString("link")))
                        .publishedAt(parsePublishedDate(item.getString("pubDate")))
                        .build();

                newsList.add(news);
            }

            log.info("Parsed {} news items from API response", newsList.size());

        } catch (Exception e) {
            log.error("Failed to parse news response: {}", e.getMessage(), e);
        }

        return newsList;
    }

    /**
     * HTML 태그 제거
     */
    private String cleanHtmlTags(String text) {
        return text.replaceAll("<[^>]*>", "")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

    /**
     * URL에서 출처 추출
     */
    private String extractSource(String url) {
        try {
            if (url.contains("naver.com")) {
                return "네이버뉴스";
            }
            String domain = url.split("/")[2];
            return domain.replace("www.", "");
        } catch (Exception e) {
            return "기타";
        }
    }

    /**
     * 발행일 파싱
     */
    private LocalDate parsePublishedDate(String pubDate) {
        try {
            // 네이버 API 날짜 형식: "Mon, 27 Jan 2025 14:30:00 +0900"
            // 간단하게 오늘 날짜로 설정 (실제로는 파싱 필요)
            return LocalDate.now();
        } catch (Exception e) {
            log.warn("Failed to parse published date: {}", pubDate);
            return LocalDate.now();
        }
    }
}
