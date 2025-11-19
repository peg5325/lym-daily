package com.formom.daily.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 설정
 *
 * Spring Cache를 사용하여 API 응답을 캐싱합니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String DAILY_SUMMARY_CACHE = "dailySummary";
    public static final String MEDIA_CACHE = "media";

    /**
     * CacheManager 빈 생성
     *
     * ConcurrentMapCacheManager: 간단한 인메모리 캐시 (개발/소규모 환경)
     * 프로덕션에서는 Redis, Ehcache 등을 고려할 수 있음
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(DAILY_SUMMARY_CACHE, MEDIA_CACHE);
    }
}
