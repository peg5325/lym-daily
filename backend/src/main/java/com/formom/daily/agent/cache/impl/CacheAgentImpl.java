package com.formom.daily.agent.cache.impl;

import com.formom.daily.agent.cache.CacheAgent;
import com.formom.daily.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * 캐시 관리 에이전트 구현체
 *
 * Spring Cache를 사용하여 캐시를 관리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheAgentImpl implements CacheAgent {

    private final CacheManager cacheManager;

    @Override
    @CacheEvict(value = {CacheConfig.DAILY_SUMMARY_CACHE, CacheConfig.MEDIA_CACHE}, allEntries = true)
    public void evictAllCaches() {
        log.info("===== CacheAgent: Evicting all caches =====");
        log.info("All caches have been cleared");
    }

    @Override
    @CacheEvict(value = CacheConfig.DAILY_SUMMARY_CACHE, allEntries = true)
    public void evictDailySummaryCache() {
        log.info("===== CacheAgent: Evicting daily summary cache =====");
        log.info("Daily summary cache has been cleared");
    }

    @Override
    @CacheEvict(value = CacheConfig.MEDIA_CACHE, allEntries = true)
    public void evictMediaCache() {
        log.info("===== CacheAgent: Evicting media cache =====");
        log.info("Media cache has been cleared");
    }
}
