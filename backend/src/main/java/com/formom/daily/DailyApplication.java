package com.formom.daily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 임영웅 Daily 메인 애플리케이션
 *
 * @author formom
 */
@SpringBootApplication
@EnableScheduling
public class DailyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyApplication.class, args);
    }
}
