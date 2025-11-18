package com.formom.daily.agent.scheduler;

/**
 * 스케줄링 에이전트 인터페이스
 *
 * 책임: 매일 정해진 시간에 뉴스 수집 → AI 요약 → TOP 3 선정 플로우를 자동 실행
 */
public interface SchedulerAgent {

    /**
     * 일일 뉴스 수집 및 요약 작업 실행
     * (NewsCollectorAgent → SummarizationAgent → ContentCuratorAgent → DB 저장)
     */
    void executeDailyNewsCollection();

    /**
     * 수동으로 뉴스 수집 실행 (테스트/관리 목적)
     */
    void executeManualCollection();
}
