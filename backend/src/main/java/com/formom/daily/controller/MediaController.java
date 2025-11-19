package com.formom.daily.controller;

import com.formom.daily.dto.MediaDto;
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
     * 최신 영상 TOP 3 조회
     *
     * GET /api/media/top3
     *
     * @return 조회수 기준 TOP 3 영상
     */
    @GetMapping("/top3")
    public ResponseEntity<List<MediaDto>> getTop3Videos() {
        log.info("GET /api/media/top3 - Fetching top 3 videos");

        List<MediaDto> top3 = mediaService.getTop3Videos();

        return ResponseEntity.ok(top3);
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
