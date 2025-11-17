package com.formom.daily.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 네이버 API 설정
 */
@Configuration
@ConfigurationProperties(prefix = "naver.api")
@Getter
@Setter
public class NaverApiConfig {

    private String clientId;
    private String clientSecret;

    /**
     * 검색 관련 설정
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "naver.search")
    @Configuration
    public static class SearchConfig {
        private String keyword = "임영웅";
        private int count = 10;
    }
}
