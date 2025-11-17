# 서브 에이전트 설계

## 개요
임영웅 Daily 프로젝트는 여러 데이터 소스에서 정보를 수집하고, AI로 처리하며, 사용자에게 제공하는 복잡한 플로우를 가지고 있습니다. 각 책임을 명확히 분리하기 위해 서브 에이전트 패턴을 적용합니다.

---

## 백엔드 서브 에이전트

### 1. NewsCollectorAgent (뉴스 수집 에이전트)
**책임**: 네이버 뉴스 API에서 임영웅 관련 뉴스 수집

**주요 기능**:
- 네이버 뉴스 검색 API 호출
- 검색 결과 파싱 및 정제
- 중복 뉴스 필터링 (URL 기준)
- 썸네일 이미지 추출
- 뉴스 메타데이터 수집 (제목, 내용, 출처, 게시일)

**인터페이스**:
```java
public interface NewsCollectorAgent {
    List<NewsDto> collectDailyNews(LocalDate date);
    List<NewsDto> searchNews(String keyword, int count);
}
```

**설정값**:
- `naver.api.client-id`: 네이버 API 클라이언트 ID
- `naver.api.client-secret`: 네이버 API 시크릿
- `naver.search.keyword`: 검색 키워드 (기본값: "임영웅")
- `naver.search.count`: 수집할 뉴스 개수 (기본값: 10)

---

### 2. MediaCollectorAgent (미디어 수집 에이전트)
**책임**: YouTube API에서 임영웅 관련 영상/사진 수집

**주요 기능**:
- YouTube Data API v3 호출
- 최신 업로드 영상 조회
- 조회수, 좋아요 수 등 메타데이터 수집
- 썸네일 URL 추출
- 공식 채널 우선순위 부여

**인터페이스**:
```java
public interface MediaCollectorAgent {
    List<MediaDto> collectLatestVideos(int count);
    List<MediaDto> collectTrendingMedia(LocalDate date);
}
```

**설정값**:
- `youtube.api.key`: YouTube API 키
- `youtube.channel.official`: 공식 채널 ID 목록
- `youtube.fetch.count`: 수집할 영상 개수 (기본값: 5)

---

### 3. SummarizationAgent (요약 에이전트)
**책임**: OpenAI API를 사용한 뉴스 요약 및 중요도 평가

**주요 기능**:
- 뉴스 본문을 40자 이내로 요약
- 중요도 점수 산정 (1-10 스케일)
- 감정 분석 (긍정/중립/부정)
- 카테고리 자동 분류 (공연, 음반, 방송 출연, 수상 등)
- 배치 처리 지원 (여러 뉴스 한 번에 요약)

**인터페이스**:
```java
public interface SummarizationAgent {
    String summarize(String content, int maxLength);
    NewsSummaryDto analyzeAndSummarize(NewsDto news);
    List<NewsSummaryDto> batchSummarize(List<NewsDto> newsList);
    int calculateImportanceScore(NewsDto news);
}
```

**프롬프트 전략**:
```
역할: 당신은 임영웅 팬을 위한 뉴스 큐레이터입니다.

요구사항:
1. 다음 뉴스를 40자 이내로 요약해주세요
2. 핵심 정보만 포함하세요 (날짜, 장소, 이벤트)
3. 팬들이 가장 궁금해할 내용 위주로 작성하세요
4. 존댓말을 사용하세요

뉴스 내용: {content}
```

**설정값**:
- `openai.api.key`: OpenAI API 키
- `openai.model`: 사용할 모델 (기본값: "gpt-3.5-turbo")
- `openai.max-tokens`: 최대 토큰 수 (기본값: 100)
- `openai.temperature`: 창의성 수준 (기본값: 0.3)

---

### 4. ContentCuratorAgent (콘텐츠 큐레이션 에이전트)
**책임**: 수집된 뉴스/미디어 중 가장 중요한 것 선정

**주요 기능**:
- 중요도 기반 뉴스 랭킹
- 상위 3개 뉴스 선정
- 중복/유사 콘텐츠 제거
- 신뢰도 낮은 소스 필터링
- 날짜별 대표 콘텐츠 선정

**선정 알고리즘**:
1. AI 중요도 점수 (40%)
2. 출처 신뢰도 (30%)
3. 최신성 (20%)
4. 사용자 반응 (조회수/댓글, 10%)

**인터페이스**:
```java
public interface ContentCuratorAgent {
    List<NewsDto> selectTopNews(List<NewsDto> newsList, int count);
    List<MediaDto> selectTopMedia(List<MediaDto> mediaList, int count);
    boolean isDuplicate(NewsDto news1, NewsDto news2);
    double calculateRelevanceScore(NewsDto news);
}
```

---

### 5. SchedulerAgent (스케줄 관리 에이전트)
**책임**: 매일 자동 실행 및 작업 오케스트레이션

**주요 기능**:
- 매일 아침 7시 자동 실행 (@Scheduled)
- 전체 데이터 수집 플로우 조율
- 에러 핸들링 및 재시도 로직
- 실행 로그 기록
- 실행 실패 시 알림 (선택)

**실행 플로우**:
```
1. NewsCollectorAgent.collectDailyNews()
2. MediaCollectorAgent.collectLatestVideos()
3. SummarizationAgent.batchSummarize()
4. ContentCuratorAgent.selectTopNews()
5. Database 저장
6. 실행 로그 기록
```

**인터페이스**:
```java
public interface SchedulerAgent {
    void runDailyCollection();
    void runManualCollection(LocalDate date);
    ExecutionLog getLastExecutionLog();
}
```

**설정값**:
- `scheduler.cron`: 실행 스케줄 (기본값: "0 0 7 * * *")
- `scheduler.retry.max-attempts`: 재시도 횟수 (기본값: 3)
- `scheduler.retry.delay-ms`: 재시도 간격 (기본값: 5000)

---

### 6. CacheAgent (캐시 관리 에이전트)
**책임**: API 호출 결과 캐싱으로 비용 절감 및 성능 향상

**주요 기능**:
- 오늘의 요약 데이터 캐싱 (1시간)
- 자주 조회되는 날짜 데이터 캐싱
- API 응답 캐싱 (중복 요청 방지)
- 캐시 무효화 전략
- 메모리 사용량 모니터링

**인터페이스**:
```java
public interface CacheAgent {
    <T> T getOrCompute(String key, Supplier<T> supplier, Duration ttl);
    void invalidate(String key);
    void invalidateAll();
    CacheStatistics getStatistics();
}
```

**캐시 전략**:
- `GET /api/today`: 1시간 캐싱
- `GET /api/date/{date}`: 과거 데이터는 24시간 캐싱
- OpenAI 요약 결과: 영구 캐싱 (동일 뉴스 재요약 방지)

---

## 프론트엔드 서브 에이전트 (컴포넌트)

### 1. DataFetchAgent (Vue Composable)
**책임**: API 호출 및 데이터 상태 관리

**주요 기능**:
- Axios를 이용한 API 호출
- 로딩/에러/성공 상태 관리
- 자동 재시도 로직
- 에러 사용자 친화적 메시지 변환

**사용 예시**:
```javascript
// composables/useDataFetch.js
export function useDataFetch() {
  const { data, loading, error, fetch } = useFetch('/api/today')
  return { todayData: data, isLoading: loading, error, refresh: fetch }
}
```

---

### 2. DateNavigationAgent
**책임**: 날짜 전환 및 네비게이션

**주요 기능**:
- 오늘/어제/특정 날짜 전환
- URL 파라미터 동기화
- 날짜 포맷팅 (한글)
- 과거 데이터 조회

---

## 에이전트 간 통신 흐름

```
[SchedulerAgent] (매일 7시)
    ↓
[NewsCollectorAgent] → 네이버 API → 10개 뉴스
    ↓
[MediaCollectorAgent] → YouTube API → 5개 영상
    ↓
[SummarizationAgent] → OpenAI API → 요약 생성
    ↓
[ContentCuratorAgent] → 중요도 평가 → 상위 3개 선정
    ↓
[Database] → 저장
    ↓
[CacheAgent] → 캐시 무효화
    ↓
[Frontend] → API 조회 → 사용자에게 표시
```

---

## 디렉토리 구조

```
backend/
├── src/main/java/com/formom/daily/
│   ├── agent/
│   │   ├── collector/
│   │   │   ├── NewsCollectorAgent.java
│   │   │   ├── MediaCollectorAgent.java
│   │   │   └── impl/
│   │   ├── ai/
│   │   │   ├── SummarizationAgent.java
│   │   │   └── impl/
│   │   ├── curator/
│   │   │   ├── ContentCuratorAgent.java
│   │   │   └── impl/
│   │   ├── scheduler/
│   │   │   ├── SchedulerAgent.java
│   │   │   └── impl/
│   │   └── cache/
│   │       ├── CacheAgent.java
│   │       └── impl/
│   ├── config/
│   │   ├── NaverApiConfig.java
│   │   ├── OpenAiConfig.java
│   │   └── SchedulerConfig.java
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── entity/

frontend/
├── src/
│   ├── composables/
│   │   ├── useDataFetch.js
│   │   ├── useDateNavigation.js
│   │   └── useErrorHandler.js
│   ├── components/
│   └── views/
```

---

## 개발 우선순위

### Phase 1: 핵심 에이전트
1. NewsCollectorAgent (Week 1)
2. SummarizationAgent (Week 2)
3. ContentCuratorAgent (Week 2)
4. SchedulerAgent (Week 2)

### Phase 2: 확장 에이전트
5. MediaCollectorAgent (Week 3)
6. CacheAgent (Week 3)

### Phase 3: 최적화
7. 재시도 로직 강화
8. 모니터링 추가
9. 성능 개선

---

## 테스트 전략

각 에이전트는 독립적으로 테스트 가능해야 합니다:

- **Unit Test**: 각 에이전트 로직 테스트
- **Integration Test**: 에이전트 간 통신 테스트
- **E2E Test**: 전체 플로우 테스트 (SchedulerAgent → Database)

**Mock 전략**:
- 외부 API는 모두 Mock 처리
- 실제 API는 별도의 Integration Test에서만 호출
- 비용이 드는 OpenAI API는 테스트 시 Mock 필수

---

## 모니터링 및 로깅

각 에이전트는 다음 정보를 로깅해야 합니다:

```java
@Slf4j
public class NewsCollectorAgentImpl implements NewsCollectorAgent {

    public List<NewsDto> collectDailyNews(LocalDate date) {
        log.info("Starting news collection for date: {}", date);

        try {
            // 수집 로직
            log.info("Successfully collected {} news articles", result.size());
            return result;
        } catch (Exception e) {
            log.error("Failed to collect news: {}", e.getMessage(), e);
            throw new CollectionException("News collection failed", e);
        }
    }
}
```

**로그 레벨**:
- INFO: 정상 실행 흐름
- WARN: 재시도 발생, 일부 실패
- ERROR: 전체 실패, 긴급 조치 필요

---

## 확장성 고려사항

### 새로운 데이터 소스 추가 시
1. 새로운 CollectorAgent 구현
2. SchedulerAgent에 플로우 추가
3. ContentCuratorAgent에 필터링 로직 추가

### 새로운 AI 기능 추가 시
1. SummarizationAgent에 메서드 추가
2. 프롬프트 템플릿 관리 (별도 파일)
3. 결과 캐싱 전략 수립

### 스케일 아웃 시
1. 각 Agent를 독립적인 마이크로서비스로 분리 가능
2. Message Queue (RabbitMQ, Kafka) 도입
3. 분산 캐싱 (Redis) 도입
