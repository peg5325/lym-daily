package com.formom.daily.dto;

import com.formom.daily.entity.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 미디어 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {

    private Long id;
    private Media.MediaType type;
    private String title;
    private String url;
    private String thumbnailUrl;
    private Long viewCount;
    private Long likeCount;
    private LocalDate publishedAt;
    private Media.VideoType videoType;
    private Boolean isNew;  // 오늘 올라온 영상 여부

    /**
     * Entity -> DTO 변환
     */
    public static MediaDto from(Media media) {
        LocalDate today = LocalDate.now();
        boolean isNew = media.getPublishedAt() != null && media.getPublishedAt().equals(today);

        return MediaDto.builder()
                .id(media.getId())
                .type(media.getType())
                .title(media.getTitle())
                .url(media.getUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .viewCount(media.getViewCount())
                .likeCount(media.getLikeCount())
                .publishedAt(media.getPublishedAt())
                .videoType(media.getVideoType())
                .isNew(isNew)
                .build();
    }

    /**
     * DTO -> Entity 변환
     */
    public Media toEntity() {
        return Media.builder()
                .type(this.type)
                .title(this.title)
                .url(this.url)
                .thumbnailUrl(this.thumbnailUrl)
                .viewCount(this.viewCount)
                .likeCount(this.likeCount)
                .publishedAt(this.publishedAt)
                .videoType(this.videoType)
                .build();
    }
}
