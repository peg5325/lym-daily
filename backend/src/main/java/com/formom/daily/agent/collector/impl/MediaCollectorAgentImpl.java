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

import java.time.Duration;
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
            "UC3WZlO2Zl8NE1yIUgtwUtQw",  // 임영웅 공식 채널 (@LYW_official)
    };

    @Override
    public List<MediaDto> collectLatestVideos(int maxResults) {
        log.info("===== MediaCollectorAgent: Collecting from Official Channel Only (maxResults: {}) =====", maxResults);

        // 공식 채널에서만 영상 수집
        List<MediaDto> officialVideos = collectFromOfficialChannels(maxResults);

        log.info("Total collected: {} videos from official channels", officialVideos.size());

        return officialVideos;
    }


    /**
     * 공식 채널에서 영상 수집
     */
    private List<MediaDto> collectFromOfficialChannels(int maxResults) {
        log.info("Collecting from official channels: {}", String.join(", ", OFFICIAL_CHANNEL_IDS));

        List<MediaDto> allChannelVideos = new ArrayList<>();

        for (String channelId : OFFICIAL_CHANNEL_IDS) {
            try {
                String url = String.format("%s/search?part=snippet&channelId=%s&type=video&order=date&maxResults=%d&key=%s",
                        YOUTUBE_API_BASE_URL, channelId, maxResults, apiKey);

                String response = webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                List<MediaDto> channelVideos = parseYouTubeSearchResponse(response, "official-" + channelId);
                allChannelVideos.addAll(channelVideos);

                log.info("Collected {} videos from channel: {}", channelVideos.size(), channelId);

            } catch (Exception e) {
                log.error("Failed to collect from channel {}: {}", channelId, e.getMessage(), e);
            }
        }

        return allChannelVideos;
    }

    /**
     * YouTube Search API 응답 파싱 (공통 로직)
     */
    private List<MediaDto> parseYouTubeSearchResponse(String response, String source) {
        if (response == null || response.isEmpty()) {
            log.warn("YouTube API returned empty response for source: {}", source);
            return new ArrayList<>();
        }

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray items = jsonResponse.optJSONArray("items");

        if (items == null || items.length() == 0) {
            log.warn("No videos found in YouTube API response for source: {}", source);
            return new ArrayList<>();
        }

        List<MediaDto> mediaDtos = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.getJSONObject(i);
                MediaDto mediaDto = parseYouTubeItem(item);

                // DB 중복 체크
                if (!mediaRepository.existsByUrl(mediaDto.getUrl())) {
                    mediaDtos.add(mediaDto);
                } else {
                    log.debug("Video already exists in DB, skipping: {}", mediaDto.getUrl());
                }
            } catch (Exception e) {
                log.error("Error parsing YouTube item from {}: {}", source, e.getMessage());
            }
        }

        return mediaDtos;
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

        // 조회수 및 영상 길이는 별도 API 호출 필요 (videos.list)
        VideoMetadata metadata = fetchVideoMetadata(videoId);

        return MediaDto.builder()
                .type(Media.MediaType.VIDEO)
                .title(title)
                .url("https://www.youtube.com/watch?v=" + videoId)
                .thumbnailUrl(thumbnailUrl)
                .viewCount(metadata.viewCount)
                .likeCount(0L)
                .publishedAt(publishedAt)
                .videoType(metadata.videoType)
                .build();
    }

    /**
     * YouTube 영상의 메타데이터 (조회수, 영상 길이) 가져오기
     */
    private VideoMetadata fetchVideoMetadata(String videoId) {
        try {
            String url = String.format("%s/videos?part=statistics,contentDetails&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, videoId, apiKey);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                return new VideoMetadata(0L, Media.VideoType.REGULAR);
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");

            if (items == null || items.length() == 0) {
                return new VideoMetadata(0L, Media.VideoType.REGULAR);
            }

            JSONObject item = items.getJSONObject(0);

            // 조회수 가져오기
            JSONObject statistics = item.getJSONObject("statistics");
            Long viewCount = statistics.optLong("viewCount", 0L);

            // 영상 길이 가져오기
            JSONObject contentDetails = item.getJSONObject("contentDetails");
            String durationStr = contentDetails.getString("duration");
            Media.VideoType videoType = classifyVideoType(durationStr);

            log.debug("Video {} - duration: {}, type: {}, viewCount: {}", videoId, durationStr, videoType, viewCount);

            return new VideoMetadata(viewCount, videoType);

        } catch (Exception e) {
            log.error("Failed to fetch video metadata for videoId: {}", videoId, e);
            return new VideoMetadata(0L, Media.VideoType.REGULAR);
        }
    }

    /**
     * ISO 8601 duration 문자열을 파싱하여 Shorts/일반 영상 분류
     *
     * @param durationStr ISO 8601 형식 (예: "PT1M30S", "PT45S")
     * @return VideoType (SHORTS: 60초 이하, REGULAR: 60초 초과)
     */
    private Media.VideoType classifyVideoType(String durationStr) {
        try {
            Duration duration = Duration.parse(durationStr);
            long seconds = duration.getSeconds();

            // 60초 이하면 Shorts, 초과하면 일반 영상
            return seconds <= 60 ? Media.VideoType.SHORTS : Media.VideoType.REGULAR;
        } catch (Exception e) {
            log.error("Failed to parse duration: {}", durationStr, e);
            return Media.VideoType.REGULAR;  // 기본값은 일반 영상
        }
    }

    /**
     * 영상 메타데이터를 담는 내부 클래스
     */
    private static class VideoMetadata {
        final Long viewCount;
        final Media.VideoType videoType;

        VideoMetadata(Long viewCount, Media.VideoType videoType) {
            this.viewCount = viewCount;
            this.videoType = videoType;
        }
    }
}
