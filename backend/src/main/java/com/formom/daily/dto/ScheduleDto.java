package com.formom.daily.dto;

import com.formom.daily.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 스케줄 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

    private Long id;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotNull(message = "일정 날짜는 필수입니다")
    private LocalDate eventDate;

    private String location;
    private String description;

    /**
     * Entity -> DTO 변환
     */
    public static ScheduleDto from(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .eventDate(schedule.getEventDate())
                .location(schedule.getLocation())
                .description(schedule.getDescription())
                .build();
    }

    /**
     * DTO -> Entity 변환
     */
    public Schedule toEntity() {
        return Schedule.builder()
                .title(this.title)
                .eventDate(this.eventDate)
                .location(this.location)
                .description(this.description)
                .build();
    }
}
