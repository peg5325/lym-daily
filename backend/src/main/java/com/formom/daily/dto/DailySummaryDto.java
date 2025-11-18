package com.formom.daily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 일일 요약 DTO
 * (오늘의 TOP 3 뉴스 + 날짜 정보)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryDto {

    private LocalDate date;
    private String formattedDate; // "2025년 1월 18일 토요일"
    private List<NewsDto> topNews; // TOP 3 뉴스
    private Integer totalNewsCount; // 전체 뉴스 개수
}
