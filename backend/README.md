# 임영웅 Daily - Backend

Spring Boot 기반 백엔드 서버

## 빌드 및 실행

### 1. 로컬 설정 파일 생성

민감한 정보(API 키, 비밀번호)는 Git에 커밋하지 않습니다.

```bash
cd backend/src/main/resources
cp application-local.yml.example application-local.yml
```

`application-local.yml` 파일을 열어서 실제 값으로 수정:

```yaml
spring:
  datasource:
    password: formom1234  # MySQL 비밀번호

naver:
  api:
    client-id: your_naver_client_id
    client-secret: your_naver_client_secret

openai:
  api:
    key: your_openai_api_key  # Week 2에서 필요

youtube:
  api:
    key: your_youtube_api_key  # Week 3에서 필요
```

### 2. Docker로 MySQL 실행

```bash
# 프로젝트 루트 디렉토리에서
docker-compose up -d

# MySQL 컨테이너 상태 확인
docker ps
```

### 3. 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
java -jar build/libs/formom-daily-0.0.1-SNAPSHOT.jar
```

## API 테스트

서버가 실행되면 다음 URL로 테스트할 수 있습니다:

- 뉴스 수집: `http://localhost:8080/api/news/collect`
- 뉴스 검색: `http://localhost:8080/api/news/search?keyword=임영웅&count=10`

## 프로젝트 구조

```
backend/
├── src/main/java/com/formom/daily/
│   ├── agent/             # 서브 에이전트들
│   │   ├── collector/     # NewsCollectorAgent, MediaCollectorAgent
│   │   ├── ai/            # SummarizationAgent
│   │   ├── curator/       # ContentCuratorAgent
│   │   ├── scheduler/     # SchedulerAgent
│   │   └── cache/         # CacheAgent
│   ├── config/            # 설정 클래스
│   ├── controller/        # REST API 컨트롤러
│   ├── service/           # 비즈니스 로직
│   ├── repository/        # JPA 리포지토리
│   ├── entity/            # JPA 엔티티
│   └── dto/               # 데이터 전송 객체
└── src/main/resources/
    └── application.yml    # 애플리케이션 설정
```

## 현재 구현 상태

### Week 1 (완료)
- ✅ Spring Boot 프로젝트 생성
- ✅ 에이전트 패키지 구조 생성
- ✅ Docker MySQL 환경 구축
- ✅ 데이터베이스 엔티티 (News, Media, Schedule)
- ✅ NewsCollectorAgent 구현
- ✅ 네이버 뉴스 API 연동 성공

### 다음 단계 (Week 2)
- SummarizationAgent (OpenAI 연동)
- ContentCuratorAgent (중요도 랭킹)
- SchedulerAgent (매일 7시 자동 실행)
