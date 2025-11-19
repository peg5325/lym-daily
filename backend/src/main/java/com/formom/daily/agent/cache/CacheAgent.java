package com.formom.daily.agent.cache;

/**
 * 캐시 관리 에이전트 인터페이스
 *
 * 오늘의 요약 데이터와 미디어를 캐싱하여 성능을 개선합니다.
 */
public interface CacheAgent {

    /**
     * 모든 캐시 무효화
     * 새로운 데이터가 수집되었을 때 호출됩니다.
     */
    void evictAllCaches();

    /**
     * 일일 요약 캐시 무효화
     */
    void evictDailySummaryCache();

    /**
     * 미디어 캐시 무효화
     */
    void evictMediaCache();
}
