package com.formom.daily.agent.ai;

import com.formom.daily.dto.NewsDto;

import java.util.List;

/**
 * AI 기반 뉴스 요약 에이전트 인터페이스
 *
 * 책임: OpenAI API를 사용하여 뉴스를 40자 이내 한글로 요약하고 중요도 점수 산정
 */
public interface SummarizationAgent {

    /**
     * 단일 뉴스 요약 및 중요도 점수 산정
     *
     * @param news 요약할 뉴스
     * @return 요약 결과 (summary, importanceScore가 업데이트된 NewsDto)
     */
    NewsDto summarizeNews(NewsDto news);

    /**
     * 여러 뉴스를 배치로 요약 및 중요도 점수 산정
     *
     * @param newsList 요약할 뉴스 목록
     * @return 요약 결과 목록
     */
    List<NewsDto> summarizeNewsBatch(List<NewsDto> newsList);

    /**
     * 중요도 점수 산정
     *
     * @param title 뉴스 제목
     * @param content 뉴스 내용
     * @return 중요도 점수 (0-100)
     */
    Integer calculateImportanceScore(String title, String content);
}
