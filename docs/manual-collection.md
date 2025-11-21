# 수동 데이터 수집 가이드

## 개요

이 문서는 자동 스케줄러가 실행되지 않았거나, 특정 날짜의 데이터를 다시 수집해야 할 때 수동으로 뉴스 및 미디어를 수집하는 방법을 설명합니다.

## 자동 스케줄러 작동 방식

- **실행 시간**: 매일 오전 7시 (KST)
- **실행 조건**: Docker 컨테이너가 실행 중이어야 함
- **수집 내용**:
  - 네이버 뉴스 검색 API를 통한 뉴스 10개
  - OpenAI API를 통한 AI 요약 및 중요도 점수 산정
  - ContentCuratorAgent를 통한 TOP 3 선정
  - YouTube API를 통한 영상 수집

## 수동 수집이 필요한 경우

1. 프로젝트가 오전 7시에 꺼져있어서 자동 수집이 실행되지 않은 경우
2. API 키가 잘못 설정되어 자동 수집이 실패한 경우
3. 데이터를 다시 수집하고 싶은 경우 (단, 중복 제거 로직으로 새로운 뉴스만 수집됨)

---

## 수집되는 데이터 항목

`/api/admin/collect` API를 호출하면 **뉴스와 YouTube 영상이 모두 자동으로 수집**됩니다.

### 수집 항목

| 데이터 유형 | 수집 개수 | API | 설명 |
|------------|----------|-----|------|
| **뉴스** | 10개 | Naver News API | 키워드 "임영웅" 검색, AI 요약 및 TOP 3 선정 |
| **YouTube 영상** | 3개 | YouTube Data API | 최신 영상 수집 (Shorts 및 일반 영상) |

### 중요 사항

- **단일 API로 통합 수집**: 현재는 뉴스와 YouTube 영상을 **별도로 수집하는 API가 없습니다**
- **모두 함께 수집**: `/api/admin/collect`를 호출하면 뉴스와 영상이 순차적으로 수집됩니다
- **독립적 실행**: 뉴스 수집이 실패해도 YouTube 영상 수집은 계속 진행됩니다

### YouTube 영상만 수집하고 싶다면?

현재는 지원하지 않습니다. 필요한 경우 다음을 고려할 수 있습니다:
- AdminController에 `POST /api/admin/collect/media` 엔드포인트 추가
- 또는 SchedulerAgentImpl의 메서드를 직접 활용

---

## 수동 수집 방법

### 방법 1: Docker 환경에서 API 호출

#### 1단계: Docker 컨테이너 시작

```bash
cd /path/to/formom
docker-compose up -d
```

**확인**:
```bash
docker-compose ps
```

모든 서비스(`formom-db`, `formom-backend`, `formom-frontend`)가 `Up` 상태여야 합니다.

#### 2단계: Backend 준비 확인

```bash
docker-compose logs backend | tail -20
```

다음 메시지가 나타나면 준비 완료:
```
Started DailyApplication in X.XXX seconds
```

#### 3단계: 수동 수집 API 호출

```bash
curl -X POST http://localhost/api/admin/collect
```

**성공 응답**:
```json
{
  "status": "success",
  "message": "뉴스 수집이 성공적으로 완료되었습니다."
}
```

**실패 응답**:
```json
{
  "status": "error",
  "message": "뉴스 수집 중 오류가 발생했습니다: [에러 메시지]"
}
```

#### 4단계: 수집 결과 확인

**로그 확인**:
```bash
docker-compose logs backend | grep -E "(Successfully|Step)" | tail -30
```

**API로 확인**:
```bash
curl http://localhost/api/today
```

---

### 방법 2: 로컬 개발 환경에서 API 호출

로컬에서 `./gradlew bootRun`으로 Backend를 실행한 경우:

```bash
curl -X POST http://localhost:8080/api/admin/collect
```

---

## 수집 프로세스 상세

수동 수집 API를 호출하면 다음 단계가 순차적으로 실행됩니다:

### Step 1: 뉴스 수집
- Naver News API 호출 (키워드: "임영웅", 개수: 10개)
- 중복 제거 (이미 DB에 있는 URL 필터링)

### Step 2: YouTube 영상 수집
- YouTube Data API 호출
- 최신 영상 3개 수집

### Step 3: AI 요약 및 중요도 점수 산정
- OpenAI API (GPT-3.5-turbo) 호출
- 각 뉴스에 대해 40자 요약 생성
- 중요도 점수 (0-100) 산정

### Step 4: TOP 3 선정
- ContentCuratorAgent가 중요도 점수 기반으로 TOP 3 선정
- 출처 신뢰도, 최신성 고려

### Step 5: 데이터베이스 저장
- 모든 뉴스 (10개) 저장
- 모든 미디어 (3개) 저장

### Step 6: 캐시 무효화
- `/api/today` 캐시 무효화

---

## 자주 묻는 질문 (FAQ)

### Q1. YouTube 영상만 따로 수집할 수 있나요?

**A**: 현재는 지원하지 않습니다. `/api/admin/collect` API는 뉴스와 YouTube 영상을 모두 수집하는 통합 API입니다.

만약 YouTube 영상만 별도로 수집하고 싶다면, 다음 방법을 고려할 수 있습니다:
1. `AdminController`에 새로운 엔드포인트 추가:
   ```java
   @PostMapping("/collect/media")
   public ResponseEntity<Map<String, String>> collectMediaOnly() {
       mediaCollectorAgent.collectTodayTop3();
       return ResponseEntity.ok(Map.of("status", "success"));
   }
   ```
2. 직접 MediaCollectorAgent 호출

### Q2. 뉴스만 따로 수집할 수 있나요?

**A**: 현재는 지원하지 않습니다. 통합 수집 API만 제공됩니다.

### Q3. 수집된 YouTube 영상은 어디서 확인할 수 있나요?

**A**: 다음 API를 통해 확인할 수 있습니다:

```bash
# Shorts와 일반 영상 각각 TOP 3씩 조회
curl http://localhost/api/media/top3

# 모든 영상 조회
curl http://localhost/api/media
```

**프론트엔드**: `http://localhost` 접속 시 홈 화면에서 자동으로 표시됩니다.

### Q4. YouTube 영상이 3개보다 적게 수집될 수 있나요?

**A**: 네, 가능합니다. 다음과 같은 경우에 3개보다 적게 수집될 수 있습니다:
- YouTube API 할당량 초과
- 공식 채널에 새로운 영상이 3개 미만인 경우
- YouTube API 키가 유효하지 않은 경우

로그를 확인하여 정확한 원인을 파악할 수 있습니다:
```bash
docker-compose logs backend | grep "media"
```

### Q5. 뉴스 수집이 실패해도 YouTube 영상은 수집되나요?

**A**: 네, 독립적으로 실행됩니다. 뉴스 수집이 실패해도 YouTube 영상 수집은 계속 진행됩니다. 반대의 경우도 마찬가지입니다.

수집 로그에서 각 단계의 성공/실패를 확인할 수 있습니다:
```bash
docker-compose logs backend | grep -E "(Step|Successfully|ERROR)"
```

### Q6. 수집 순서를 바꿀 수 있나요? (YouTube를 먼저 수집)

**A**: 코드 수정이 필요합니다. `SchedulerAgentImpl.java:81-143` 파일의 `executeNewsCollectionFlow` 메서드에서 Step 1(뉴스)와 Step 2(YouTube)의 순서를 변경하면 됩니다.

하지만 현재 구조에서는 뉴스를 먼저 수집하는 것을 권장합니다. AI 요약에 시간이 오래 걸리므로, 뉴스 수집 중 문제가 발생해도 YouTube 영상은 독립적으로 수집될 수 있도록 설계되었습니다.

---

## 트러블슈팅

### 1. API 호출 시 401 Unauthorized 에러

**원인**: Naver API 키가 유효하지 않음

**해결**:
1. [Naver Developers](https://developers.naver.com/apps/#/list) 콘솔 접속
2. 애플리케이션에서 Client ID와 Client Secret 확인/재발급
3. `.env` 파일 업데이트:
   ```bash
   NAVER_API_KEY=새로운_Client_ID
   NAVER_API_SECRET=새로운_Client_Secret
   ```
4. Docker 컨테이너 재시작:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

**관련 문서**: `docs/troubleshooting/api/naver-401-error.md`

### 2. OpenAI API 에러

**원인**: OpenAI API 키가 유효하지 않거나, 할당량 초과

**해결**:
1. [OpenAI Platform](https://platform.openai.com/api-keys) 접속
2. API 키 확인 및 사용량 체크
3. `.env` 파일 업데이트:
   ```bash
   OPENAI_API_KEY=새로운_API_키
   ```
4. Docker 컨테이너 재시작

### 3. YouTube API 에러

**원인**: YouTube API 키가 유효하지 않거나, 할당량 초과

**해결**:
1. [Google Cloud Console](https://console.cloud.google.com/apis/credentials) 접속
2. API 키 확인
3. `.env` 파일 업데이트:
   ```bash
   YOUTUBE_API_KEY=새로운_API_키
   ```
4. Docker 컨테이너 재시작

### 4. 중복 뉴스만 수집되어 새로운 데이터가 없음

**원인**: 이미 해당 URL이 DB에 존재하여 중복 제거됨

**확인**:
```bash
docker-compose logs backend | grep "filtered"
```

출력 예시:
```
Successfully collected 0 news articles (filtered 10 duplicates)
```

**해결**: 정상 동작입니다. 새로운 뉴스가 없는 경우입니다.

### 5. Docker 컨테이너가 시작되지 않음

**확인**:
```bash
docker-compose logs
```

**일반적인 해결책**:
- 포트 충돌 확인 (80, 8080, 3306)
- `.env` 파일 존재 확인
- Docker 데몬 실행 확인

---

## 수집 로그 예시

### 성공적인 수집 로그

```
2025-11-21 05:22:59.211  INFO --- ===== Starting Manual News Collection =====
2025-11-21 05:22:59.211  INFO --- Step 1: Collecting news from Naver API
2025-11-21 05:22:59.788  INFO --- Parsed 10 news items from API response
2025-11-21 05:22:59.802  INFO --- Successfully collected 10 news articles (filtered 0 duplicates)
2025-11-21 05:22:59.802  INFO --- Step 3: Summarizing news with AI
2025-11-21 05:23:04.140  INFO --- Successfully summarized news. Summary: ..., Score: 95
2025-11-21 05:23:07.964  INFO --- Successfully summarized news. Summary: ..., Score: 82
...
2025-11-21 05:23:22.651  INFO --- Successfully summarized 10 news articles
2025-11-21 05:23:22.651  INFO --- Step 4: Selecting TOP 3 news
2025-11-21 05:23:22.666  INFO --- Step 6: Saving all news to database
2025-11-21 05:23:22.714  INFO --- Successfully saved 10 news articles to database
2025-11-21 05:23:22.714  INFO --- Step 2: Collecting videos from YouTube API
2025-11-21 05:23:25.978  INFO --- Step 5: Saving all media to database
2025-11-21 05:23:25.985  INFO --- Successfully saved 3 videos to database
2025-11-21 05:23:25.985  INFO --- Step 7: Invalidating all caches
```

---

## 자동화 팁

### cron으로 정기적으로 확인

서버가 계속 실행되고 있다면 굳이 필요하지 않지만, 만약을 대비해 cron으로 정기적으로 수동 수집을 실행할 수 있습니다:

```bash
# crontab -e
0 7 * * * curl -X POST http://localhost/api/admin/collect
```

### 수집 실패 알림 설정

수집이 실패하면 알림을 받도록 설정:

```bash
#!/bin/bash
RESPONSE=$(curl -s -X POST http://localhost/api/admin/collect)
STATUS=$(echo $RESPONSE | jq -r '.status')

if [ "$STATUS" != "success" ]; then
    # 알림 전송 (예: Slack, 이메일 등)
    echo "Data collection failed: $RESPONSE"
fi
```

---

## 참고 자료

- **스케줄러 구현**: `backend/src/main/java/com/formom/daily/agent/scheduler/impl/SchedulerAgentImpl.java`
- **관리자 API**: `backend/src/main/java/com/formom/daily/controller/AdminController.java`
- **뉴스 수집 Agent**: `backend/src/main/java/com/formom/daily/agent/collector/impl/NewsCollectorAgentImpl.java`
- **미디어 수집 Agent**: `backend/src/main/java/com/formom/daily/agent/collector/impl/MediaCollectorAgentImpl.java`
- **AI 요약 Agent**: `backend/src/main/java/com/formom/daily/agent/ai/impl/SummarizationAgentImpl.java`
- **큐레이션 Agent**: `backend/src/main/java/com/formom/daily/agent/curator/impl/ContentCuratorAgentImpl.java`

---

## 문의

문제가 지속되면 다음을 확인하세요:

1. Backend 로그: `docker-compose logs backend`
2. Database 로그: `docker-compose logs db`
3. 환경 변수 확인: `docker exec formom-backend env | grep -E "NAVER|YOUTUBE|OPENAI"`
