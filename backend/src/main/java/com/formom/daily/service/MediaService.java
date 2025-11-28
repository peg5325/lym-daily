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
     * 모든 영상 조회
     */
    @Transactional(readOnly = true)
    public List<MediaDto> getAllVideos() {
        log.info("Getting all videos");

        List<Media> mediaList = mediaRepository.findTop9ByTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO);

        return mediaList.stream()
                .map(MediaDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 최신 영상 조회 (공식 채널 영상, 최대 9개)
     */
    @Transactional(readOnly = true)
    public MediaGroupResponse getGroupedVideos() {
        log.info("Getting latest videos from official channel");

        // 최신 영상 최대 9개 (발행일 순)
        List<MediaDto> videos = mediaRepository
                .findTop9ByTypeOrderByPublishedAtDesc(Media.MediaType.VIDEO)
                .stream()
                .map(MediaDto::from)
                .collect(Collectors.toList());

        log.info("Found {} videos", videos.size());

        // 모든 영상을 shorts에 담아서 반환 (프론트엔드 호환성 유지)
        return MediaGroupResponse.builder()
                .shorts(videos)
                .regularVideos(List.of())  // 빈 리스트
                .build();
    }
}
