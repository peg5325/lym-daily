package com.formom.daily.agent.collector;

import com.formom.daily.dto.NewsDto;

import java.time.LocalDate;
import java.util.List;

/**
 * 뉴스 수집 에이전트 인터페이스
 *
 * 책임: 네이버 뉴스 API에서 임영웅 관련 뉴스 수집
 */
public interface NewsCollectorAgent {

    /**
     * 특정 날짜의 일일 뉴스 수집
     *
     * @param date 수집할 날짜
     * @return 수집된 뉴스 목록
     */
    List<NewsDto> collectDailyNews(LocalDate date);

    /**
     * 키워드로 뉴스 검색
     *
     * @param keyword 검색 키워드
     * @param count 수집할 개수
     * @return 검색된 뉴스 목록
     */
    List<NewsDto> searchNews(String keyword, int count);
}
