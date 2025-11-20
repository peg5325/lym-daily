package com.formom.daily.agent.collector;

import com.formom.daily.dto.MediaDto;

import java.time.LocalDate;
import java.util.List;

/**
 * 미디어 수집 에이전트 인터페이스
 *
 * YouTube API를 통해 임영웅 관련 영상을 수집합니다.
 */
public interface MediaCollectorAgent {

    /**
     * 최신 YouTube 영상 수집
     *
     * @param maxResults 수집할 최대 영상 개수
     * @return 수집된 영상 목록
     */
    List<MediaDto> collectLatestVideos(int maxResults);

    /**
     * 특정 날짜의 YouTube 영상 수집
     *
     * @param date 수집할 날짜
     * @param maxResults 수집할 최대 영상 개수
     * @return 수집된 영상 목록
     */
    List<MediaDto> collectVideosByDate(LocalDate date, int maxResults);

    /**
     * 오늘의 YouTube 영상 TOP 3 수집
     *
     * @return 수집된 영상 목록 (최대 3개)
     */
    List<MediaDto> collectTodayTop3();
}
