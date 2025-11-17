package com.formom.daily.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 뉴스 엔티티
 */
@Entity
@Table(name = "news", indexes = {
        @Index(name = "idx_published_at", columnList = "published_at"),
        @Index(name = "idx_importance_score", columnList = "importance_score")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 200)
    private String summary;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, unique = true, length = 1000)
    private String url;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(name = "published_at", nullable = false)
    private LocalDate publishedAt;

    @Column(name = "importance_score")
    private Integer importanceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public News(String title, String content, String summary, String source,
                String url, String thumbnailUrl, LocalDate publishedAt, Integer importanceScore) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.source = source;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
        this.importanceScore = importanceScore;
    }

    public void updateSummary(String summary, Integer importanceScore) {
        this.summary = summary;
        this.importanceScore = importanceScore;
    }
}
