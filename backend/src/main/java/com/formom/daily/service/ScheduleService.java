package com.formom.daily.service;

import com.formom.daily.dto.ScheduleDto;
import com.formom.daily.entity.Schedule;
import com.formom.daily.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 스케줄 서비스
 *
 * 스케줄 CRUD 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 스케줄 생성
     */
    @Transactional
    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        log.info("Creating new schedule: {}", scheduleDto.getTitle());

        Schedule schedule = scheduleDto.toEntity();
        Schedule savedSchedule = scheduleRepository.save(schedule);

        log.info("Successfully created schedule with id: {}", savedSchedule.getId());
        return ScheduleDto.from(savedSchedule);
    }

    /**
     * 스케줄 조회 (ID)
     */
    @Transactional(readOnly = true)
    public ScheduleDto getSchedule(Long id) {
        log.info("Getting schedule with id: {}", id);

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + id));

        return ScheduleDto.from(schedule);
    }

    /**
     * 이번 주 스케줄 조회
     * 오늘 기준으로 일요일까지의 스케줄
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getWeekSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        log.info("Getting week schedules from {} to {}", today, endOfWeek);

        List<Schedule> schedules = scheduleRepository.findByEventDateBetweenOrderByEventDateAsc(today, endOfWeek);

        log.info("Found {} schedules for this week", schedules.size());

        return schedules.stream()
                .map(ScheduleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 기간 스케줄 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByPeriod(LocalDate startDate, LocalDate endDate) {
        log.info("Getting schedules from {} to {}", startDate, endDate);

        List<Schedule> schedules = scheduleRepository.findByEventDateBetweenOrderByEventDateAsc(startDate, endDate);

        log.info("Found {} schedules in the period", schedules.size());

        return schedules.stream()
                .map(ScheduleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 스케줄 수정
     */
    @Transactional
    public ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto) {
        log.info("Updating schedule with id: {}", id);

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + id));

        schedule.update(
                scheduleDto.getTitle(),
                scheduleDto.getEventDate(),
                scheduleDto.getLocation(),
                scheduleDto.getDescription()
        );

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        log.info("Successfully updated schedule with id: {}", id);
        return ScheduleDto.from(updatedSchedule);
    }

    /**
     * 스케줄 삭제
     */
    @Transactional
    public void deleteSchedule(Long id) {
        log.info("Deleting schedule with id: {}", id);

        if (!scheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Schedule not found with id: " + id);
        }

        scheduleRepository.deleteById(id);

        log.info("Successfully deleted schedule with id: {}", id);
    }

    /**
     * 모든 스케줄 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getAllSchedules() {
        log.info("Getting all schedules");

        List<Schedule> schedules = scheduleRepository.findAll();

        return schedules.stream()
                .map(ScheduleDto::from)
                .collect(Collectors.toList());
    }
}
