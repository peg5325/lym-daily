package com.formom.daily.repository;

import com.formom.daily.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 미디어 리포지토리
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * URL로 미디어 조회 (중복 체크용)
     */
    Optional<Media> findByUrl(String url);

    /**
     * 특정 날짜의 미디어 조회
     */
    List<Media> findByPublishedAtOrderByViewCountDesc(LocalDate publishedAt);

    /**
     * 특정 타입의 최신 미디어 조회 (5개)
     */
    List<Media> findTop5ByTypeOrderByPublishedAtDesc(Media.MediaType type);

    /**
     * 특정 타입의 최신 미디어 조회 (10개)
     */
    List<Media> findTop9ByTypeOrderByPublishedAtDesc(Media.MediaType type);

    /**
     * 특정 타입 및 영상 타입의 최신 미디어 조회
     */
    List<Media> findTop10ByTypeAndVideoTypeOrderByPublishedAtDesc(Media.MediaType type, Media.VideoType videoType);

    /**
     * URL 존재 여부 확인
     */
    boolean existsByUrl(String url);
}
