package com.formom.daily.repository;

import com.formom.daily.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 뉴스 리포지토리
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * URL로 뉴스 조회 (중복 체크용)
     */
    Optional<News> findByUrl(String url);

    /**
     * 특정 날짜의 뉴스 조회
     */
    List<News> findByPublishedAtOrderByImportanceScoreDesc(LocalDate publishedAt);

    /**
     * 특정 날짜의 상위 N개 뉴스 조회
     */
    List<News> findTop3ByPublishedAtOrderByImportanceScoreDesc(LocalDate publishedAt);

    /**
     * URL 존재 여부 확인
     */
    boolean existsByUrl(String url);
}
