package com.formom.daily.dto;

import com.formom.daily.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 뉴스 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String source;
    private String url;
    private String thumbnailUrl;
    private LocalDate publishedAt;
    private Integer importanceScore;

    /**
     * Entity -> DTO 변환
     */
    public static NewsDto fromEntity(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .summary(news.getSummary())
                .source(news.getSource())
                .url(news.getUrl())
                .thumbnailUrl(news.getThumbnailUrl())
                .publishedAt(news.getPublishedAt())
                .importanceScore(news.getImportanceScore())
                .build();
    }

    /**
     * DTO -> Entity 변환
     */
    public News toEntity() {
        return News.builder()
                .title(this.title)
                .content(this.content)
                .summary(this.summary)
                .source(this.source)
                .url(this.url)
                .thumbnailUrl(this.thumbnailUrl)
                .publishedAt(this.publishedAt)
                .importanceScore(this.importanceScore)
                .build();
    }
}
