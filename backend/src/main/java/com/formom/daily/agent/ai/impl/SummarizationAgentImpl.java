package com.formom.daily.agent.ai.impl;

import com.formom.daily.agent.ai.SummarizationAgent;
import com.formom.daily.config.OpenAiConfig;
import com.formom.daily.dto.NewsDto;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 기반 뉴스 요약 에이전트 구현체
 *
 * OpenAI GPT-3.5 Turbo를 사용하여 뉴스를 요약하고 중요도 점수를 산정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummarizationAgentImpl implements SummarizationAgent {

    private final OpenAiService openAiService;
    private final OpenAiConfig openAiConfig;

    // 요약 프롬프트
    private static final String SUMMARY_PROMPT_TEMPLATE =
            "다음 뉴스를 40자 이내의 한글로 요약해주세요. " +
            "팬들이 '왜 이 소식이 중요한가'를 바로 알 수 있도록 핵심만 전달하세요.\n\n" +
            "제목: %s\n" +
            "내용: %s\n\n" +
            "요약 (40자 이내):";

    // 중요도 점수 산정 프롬프트
    private static final String IMPORTANCE_PROMPT_TEMPLATE =
            "다음 뉴스의 중요도를 0-100 사이의 숫자로 평가해주세요. " +
            "평가 기준:\n" +
            "- 90-100: 콘서트, 음반 발매, 수상 등 팬들이 꼭 알아야 할 중요한 소식\n" +
            "- 70-89: 방송 출연, 행사 참여, 인터뷰 등 관심있는 소식\n" +
            "- 50-69: 일반적인 뉴스, 기사\n" +
            "- 30-49: 부차적인 소식\n" +
            "- 0-29: 중요도가 낮은 소식\n\n" +
            "제목: %s\n" +
            "내용: %s\n\n" +
            "중요도 점수 (0-100, 숫자만 답변):";

    @Override
    public NewsDto summarizeNews(NewsDto news) {
        log.info("Starting news summarization for: {}", news.getTitle());

        try {
            // 1. 뉴스 요약 생성
            String summary = generateSummary(news.getTitle(), news.getContent());

            // 2. 중요도 점수 산정
            Integer importanceScore = calculateImportanceScore(news.getTitle(), news.getContent());

            // 3. NewsDto에 결과 반영
            news.setSummary(summary);
            news.setImportanceScore(importanceScore);

            log.info("Successfully summarized news. Summary: {}, Score: {}", summary, importanceScore);

            return news;

        } catch (Exception e) {
            log.error("Failed to summarize news: {}", e.getMessage(), e);
            // 실패 시 기본값 설정
            news.setSummary(truncateContent(news.getTitle(), 40));
            news.setImportanceScore(50);
            return news;
        }
    }

    @Override
    public List<NewsDto> summarizeNewsBatch(List<NewsDto> newsList) {
        log.info("Starting batch summarization for {} news items", newsList.size());

        return newsList.stream()
                .map(this::summarizeNews)
                .collect(Collectors.toList());
    }

    @Override
    public Integer calculateImportanceScore(String title, String content) {
        try {
            String prompt = String.format(IMPORTANCE_PROMPT_TEMPLATE, title, content);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiConfig.getModel())
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(),
                                    "당신은 임영웅 관련 뉴스의 중요도를 평가하는 전문가입니다. 숫자만 답변하세요."),
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)
                    ))
                    .maxTokens(10)
                    .temperature(openAiConfig.getTemperature())
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent().trim();

            // 숫자만 추출
            Integer score = Integer.parseInt(response.replaceAll("[^0-9]", ""));

            // 0-100 범위로 제한
            return Math.max(0, Math.min(100, score));

        } catch (Exception e) {
            log.error("Failed to calculate importance score: {}", e.getMessage(), e);
            return 50; // 기본값
        }
    }

    /**
     * OpenAI API를 사용하여 뉴스 요약 생성
     */
    private String generateSummary(String title, String content) {
        try {
            String prompt = String.format(SUMMARY_PROMPT_TEMPLATE, title, content);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiConfig.getModel())
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(),
                                    "당신은 임영웅 팬들을 위한 뉴스 요약 전문가입니다. 40자 이내로 핵심만 간결하게 요약하세요."),
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)
                    ))
                    .maxTokens(openAiConfig.getMaxTokens())
                    .temperature(openAiConfig.getTemperature())
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            String summary = result.getChoices().get(0).getMessage().getContent().trim();

            // 40자 제한 적용
            return truncateContent(summary, 40);

        } catch (Exception e) {
            log.error("Failed to generate summary: {}", e.getMessage(), e);
            return truncateContent(title, 40);
        }
    }

    /**
     * 텍스트를 지정된 길이로 자르기
     */
    private String truncateContent(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
