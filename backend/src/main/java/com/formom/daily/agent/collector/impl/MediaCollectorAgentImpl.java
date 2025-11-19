package com.formom.daily.agent.collector.impl;

import com.formom.daily.agent.collector.MediaCollectorAgent;
import com.formom.daily.dto.MediaDto;
import com.formom.daily.entity.Media;
import com.formom.daily.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 미디어 수집 에이전트 구현체
 *
 * YouTube Data API v3를 사용하여 임영웅 관련 영상을 수집합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediaCollectorAgentImpl implements MediaCollectorAgent {

    private final MediaRepository mediaRepository;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private static final String SEARCH_QUERY = "임영웅";
    private static final String[] OFFICIAL_CHANNEL_IDS = {
            "UCWkzQdmU-1O0Ke-tt2pDmNw",  // 임영웅 공식 채널 (예시)
    };

    @Override
    public List<MediaDto> collectLatestVideos(int maxResults) {
        log.info("===== MediaCollectorAgent: Collecting latest videos (maxResults: {}) =====", maxResults);

        try {
            // YouTube API 호출
            String url = String.format("%s/search?part=snippet&q=%s&type=video&order=date&maxResults=%d&key=%s",
                    YOUTUBE_API_BASE_URL, SEARCH_QUERY, maxResults, apiKey);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("YouTube API returned empty response");
                return new ArrayList<>();
            }

            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");

            if (items == null || items.length() == 0) {
                log.warn("No videos found in YouTube API response");
                return new ArrayList<>();
            }

            // MediaDto 리스트로 변환
            List<MediaDto> mediaDtos = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    MediaDto mediaDto = parseYouTubeItem(item);

                    // 중복 체크
                    if (!mediaRepository.existsByUrl(mediaDto.getUrl())) {
                        mediaDtos.add(mediaDto);
                    } else {
                        log.debug("Duplicate video skipped: {}", mediaDto.getUrl());
                    }
                } catch (Exception e) {
                    log.error("Error parsing YouTube item: {}", e.getMessage());
                }
            }

            log.info("Collected {} new videos from YouTube", mediaDtos.size());
            return mediaDtos;

        } catch (Exception e) {
            log.error("Failed to collect YouTube videos: {}", e.getMessage(), e);

            // Fallback: 빈 목록 반환
            log.warn("Returning empty list due to API failure");
            return new ArrayList<>();
        }
    }

    @Override
    public List<MediaDto> collectVideosByDate(LocalDate date, int maxResults) {
        log.info("===== MediaCollectorAgent: Collecting videos by date ({}) =====", date);

        try {
            // 날짜 범위 설정 (해당 날짜의 00:00 ~ 23:59)
            ZonedDateTime publishedAfter = date.atStartOfDay().atZone(java.time.ZoneId.of("Asia/Seoul"));
            ZonedDateTime publishedBefore = date.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.of("Asia/Seoul"));

            String afterStr = publishedAfter.format(DateTimeFormatter.ISO_INSTANT);
            String beforeStr = publishedBefore.format(DateTimeFormatter.ISO_INSTANT);

            // YouTube API 호출
            String url = String.format(
                    "%s/search?part=snippet&q=%s&type=video&order=date&maxResults=%d&publishedAfter=%s&publishedBefore=%s&key=%s",
                    YOUTUBE_API_BASE_URL, SEARCH_QUERY, maxResults, afterStr, beforeStr, apiKey);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("YouTube API returned empty response");
                return new ArrayList<>();
            }

            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");

            if (items == null || items.length() == 0) {
                log.warn("No videos found for date: {}", date);
                return new ArrayList<>();
            }

            // MediaDto 리스트로 변환
            List<MediaDto> mediaDtos = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    MediaDto mediaDto = parseYouTubeItem(item);

                    if (!mediaRepository.existsByUrl(mediaDto.getUrl())) {
                        mediaDtos.add(mediaDto);
                    }
                } catch (Exception e) {
                    log.error("Error parsing YouTube item: {}", e.getMessage());
                }
            }

            log.info("Collected {} videos for date: {}", mediaDtos.size(), date);
            return mediaDtos;

        } catch (Exception e) {
            log.error("Failed to collect YouTube videos by date: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MediaDto> collectTodayTop3() {
        log.info("===== MediaCollectorAgent: Collecting today's TOP 3 videos =====");

        // 최신 영상 5개 수집
        List<MediaDto> videos = collectLatestVideos(5);

        if (videos.isEmpty()) {
            log.warn("No videos collected for today");
            return new ArrayList<>();
        }

        // 조회수 기준으로 정렬 후 상위 3개 선택
        List<MediaDto> top3 = videos.stream()
                .sorted((v1, v2) -> {
                    Long count1 = v1.getViewCount() != null ? v1.getViewCount() : 0L;
                    Long count2 = v2.getViewCount() != null ? v2.getViewCount() : 0L;
                    return count2.compareTo(count1);
                })
                .limit(3)
                .collect(Collectors.toList());

        log.info("Selected TOP 3 videos from {} candidates", videos.size());
        return top3;
    }

    /**
     * YouTube API 응답 아이템을 MediaDto로 변환
     */
    private MediaDto parseYouTubeItem(JSONObject item) {
        JSONObject snippet = item.getJSONObject("snippet");
        JSONObject id = item.getJSONObject("id");

        String videoId = id.getString("videoId");
        String title = snippet.getString("title");
        String thumbnailUrl = snippet.getJSONObject("thumbnails")
                .getJSONObject("high")
                .getString("url");

        // 게시일 파싱
        String publishedAtStr = snippet.getString("publishedAt");
        LocalDate publishedAt = ZonedDateTime.parse(publishedAtStr).toLocalDate();

        // 조회수 및 좋아요 수는 별도 API 호출 필요 (videos.list)
        // 간단한 구현을 위해 일단 0으로 설정하고, 추후 개선 가능
        Long viewCount = fetchVideoStatistics(videoId);

        return MediaDto.builder()
                .type(Media.MediaType.VIDEO)
                .title(title)
                .url("https://www.youtube.com/watch?v=" + videoId)
                .thumbnailUrl(thumbnailUrl)
                .viewCount(viewCount)
                .likeCount(0L)
                .publishedAt(publishedAt)
                .build();
    }

    /**
     * YouTube 영상의 통계 정보 (조회수, 좋아요 수) 가져오기
     */
    private Long fetchVideoStatistics(String videoId) {
        try {
            String url = String.format("%s/videos?part=statistics&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, videoId, apiKey);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                return 0L;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");

            if (items == null || items.length() == 0) {
                return 0L;
            }

            JSONObject statistics = items.getJSONObject(0).getJSONObject("statistics");
            return statistics.optLong("viewCount", 0L);

        } catch (Exception e) {
            log.error("Failed to fetch video statistics for videoId: {}", videoId, e);
            return 0L;
        }
    }
}
