package com.formom.daily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Shorts와 일반 영상을 그룹화한 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaGroupResponse {

    private List<MediaDto> shorts;          // Shorts 영상 (60초 이하)
    private List<MediaDto> regularVideos;   // 일반 영상 (60초 초과)
}
