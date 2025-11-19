package com.formom.daily.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 미디어 엔티티 (YouTube 영상, 사진 등)
 */
@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_published_at", columnList = "published_at"),
        @Index(name = "idx_type", columnList = "type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType type;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, unique = true, length = 1000)
    private String url;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "published_at", nullable = false)
    private LocalDate publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_type", length = 20)
    private VideoType videoType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Media(MediaType type, String title, String url, String thumbnailUrl,
                 Long viewCount, Long likeCount, LocalDate publishedAt, VideoType videoType) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
        this.videoType = videoType;
    }

    public enum MediaType {
        VIDEO, PHOTO
    }

    public enum VideoType {
        SHORTS,    // 60초 이하 영상
        REGULAR    // 일반 영상
    }
}
