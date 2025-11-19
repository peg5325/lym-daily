package com.formom.daily.service;

import com.formom.daily.dto.MediaDto;
import com.formom.daily.dto.MediaGroupResponse;
import com.formom.daily.entity.Media;
import com.formom.daily.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 미디어 서비스
 *
 * YouTube 영상 조회 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    /**
     * 최신 영상 TOP 3 조회
     */
    @Transactional(readOnly = true)
    public List<MediaDto> getTop3Videos() {
        log.info("Getting top 3 latest videos");

        List<Media> mediaList = mediaRepository.findTop5ByTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO);

        if (mediaList.isEmpty()) {
            log.warn("No videos found in database");
            return List.of();
        }

        // 최신 영상 중 조회수 기준 상위 3개 선택
        List<MediaDto> top3 = mediaList.stream()
                .sorted((m1, m2) -> {
                    Long count1 = m1.getViewCount() != null ? m1.getViewCount() : 0L;
                    Long count2 = m2.getViewCount() != null ? m2.getViewCount() : 0L;
                    return count2.compareTo(count1);
                })
                .limit(3)
                .map(MediaDto::from)
                .collect(Collectors.toList());

        log.info("Found {} videos, selected TOP 3", mediaList.size());
        return top3;
    }

    /**
     * 모든 영상 조회
     */
    @Transactional(readOnly = true)
    public List<MediaDto> getAllVideos() {
        log.info("Getting all videos");

        List<Media> mediaList = mediaRepository.findTop5ByTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO);

        return mediaList.stream()
                .map(MediaDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Shorts와 일반 영상을 각각 TOP 3씩 조회
     */
    @Transactional(readOnly = true)
    public MediaGroupResponse getGroupedVideos() {
        log.info("Getting grouped videos (Shorts and Regular)");

        // Shorts TOP 3 (조회수 순)
        List<MediaDto> shorts = mediaRepository
                .findTop10ByTypeAndVideoTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO, Media.VideoType.SHORTS)
                .stream()
                .sorted((m1, m2) -> {
                    Long count1 = m1.getViewCount() != null ? m1.getViewCount() : 0L;
                    Long count2 = m2.getViewCount() != null ? m2.getViewCount() : 0L;
                    return count2.compareTo(count1);
                })
                .limit(3)
                .map(MediaDto::from)
                .collect(Collectors.toList());

        // 일반 영상 TOP 3 (조회수 순)
        List<MediaDto> regularVideos = mediaRepository
                .findTop10ByTypeAndVideoTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO, Media.VideoType.REGULAR)
                .stream()
                .sorted((m1, m2) -> {
                    Long count1 = m1.getViewCount() != null ? m1.getViewCount() : 0L;
                    Long count2 = m2.getViewCount() != null ? m2.getViewCount() : 0L;
                    return count2.compareTo(count1);
                })
                .limit(3)
                .map(MediaDto::from)
                .collect(Collectors.toList());

        log.info("Found {} Shorts and {} Regular videos", shorts.size(), regularVideos.size());

        return MediaGroupResponse.builder()
                .shorts(shorts)
                .regularVideos(regularVideos)
                .build();
    }
}
