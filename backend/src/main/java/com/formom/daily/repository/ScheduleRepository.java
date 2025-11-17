package com.formom.daily.repository;

import com.formom.daily.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 스케줄 리포지토리
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 특정 기간의 스케줄 조회
     */
    List<Schedule> findByEventDateBetweenOrderByEventDateAsc(LocalDate startDate, LocalDate endDate);

    /**
     * 특정 날짜 이후의 스케줄 조회
     */
    List<Schedule> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);

    /**
     * 이번 주 스케줄 조회용
     */
    List<Schedule> findByEventDateBetween(LocalDate startDate, LocalDate endDate);
}
