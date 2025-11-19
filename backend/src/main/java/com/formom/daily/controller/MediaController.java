package com.formom.daily.controller;

import com.formom.daily.dto.MediaDto;
import com.formom.daily.dto.MediaGroupResponse;
import com.formom.daily.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 미디어 API 컨트롤러
 *
 * YouTube 영상 조회 엔드포인트 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * Shorts와 일반 영상 각각 TOP 3씩 조회
     *
     * GET /api/media/top3
     *
     * @return Shorts 3개 + 일반 영상 3개
     */
    @GetMapping("/top3")
    public ResponseEntity<MediaGroupResponse> getTop3Videos() {
        log.info("GET /api/media/top3 - Fetching grouped videos (Shorts + Regular)");

        MediaGroupResponse groupedVideos = mediaService.getGroupedVideos();

        return ResponseEntity.ok(groupedVideos);
    }

    /**
     * 모든 영상 조회
     *
     * GET /api/media
     *
     * @return 모든 영상 목록
     */
    @GetMapping
    public ResponseEntity<List<MediaDto>> getAllVideos() {
        log.info("GET /api/media - Fetching all videos");

        List<MediaDto> videos = mediaService.getAllVideos();

        return ResponseEntity.ok(videos);
    }
}
