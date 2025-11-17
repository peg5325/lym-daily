package com.formom.daily.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 스케줄 엔티티
 */
@Entity
@Table(name = "schedule", indexes = {
        @Index(name = "idx_event_date", columnList = "event_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Schedule(String title, LocalDate eventDate, String location, String description) {
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.description = description;
    }

    public void update(String title, LocalDate eventDate, String location, String description) {
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.description = description;
    }
}
