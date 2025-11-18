package com.formom.daily.agent.curator;

import com.formom.daily.dto.NewsDto;

import java.util.List;

/**
 * 콘텐츠 큐레이션 에이전트 인터페이스
 *
 * 책임: 중요도 기반으로 뉴스를 랭킹하고 상위 3개 선정
 */
public interface ContentCuratorAgent {

    /**
     * 중요도 기반으로 상위 N개 뉴스 선정
     *
     * @param newsList 뉴스 목록
     * @param topN 선정할 개수
     * @return 상위 N개 뉴스 (중요도 순)
     */
    List<NewsDto> selectTopNews(List<NewsDto> newsList, int topN);

    /**
     * 오늘의 TOP 3 뉴스 선정
     *
     * @param newsList 뉴스 목록
     * @return TOP 3 뉴스
     */
    List<NewsDto> selectTodayTop3(List<NewsDto> newsList);

    /**
     * 중복 콘텐츠 제거
     *
     * @param newsList 뉴스 목록
     * @return 중복 제거된 뉴스 목록
     */
    List<NewsDto> removeDuplicates(List<NewsDto> newsList);

    /**
     * 뉴스 랭킹 점수 계산
     * (중요도, 출처 신뢰도, 최신성 종합)
     *
     * @param news 뉴스
     * @return 랭킹 점수
     */
    Double calculateRankingScore(NewsDto news);
}
