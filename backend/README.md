# 임영웅 Daily - Backend

Spring Boot 기반 백엔드 서버

## 빌드 및 실행

### 1. Gradle Wrapper 생성 (최초 1회)

```bash
# Gradle이 설치되어 있다면
gradle wrapper --gradle-version 7.6

# 또는 IntelliJ IDEA에서 프로젝트를 열면 자동 생성됩니다
```

### 2. 환경 변수 설정

`.env` 파일을 backend 디렉토리에 생성하고 다음 내용을 입력하세요:

```env
# Naver API
NAVER_API_KEY=your_naver_client_id
NAVER_API_SECRET=your_naver_client_secret

# YouTube API
YOUTUBE_API_KEY=your_youtube_api_key

# OpenAI API
OPENAI_API_KEY=your_openai_api_key

# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=formom
DB_USERNAME=root
DB_PASSWORD=your_password
```

### 3. MySQL 데이터베이스 생성

```sql
CREATE DATABASE formom CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 빌드 및 실행

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

### Week 1 (진행 중)
- ✅ Spring Boot 프로젝트 생성
- ✅ 에이전트 패키지 구조 생성
- ✅ MySQL 연결 설정
- ✅ 데이터베이스 엔티티 (News, Media, Schedule)
- ✅ NewsCollectorAgent 구현
- 🚧 네이버 뉴스 API 연동 테스트 (API 키 필요)

### 다음 단계 (Week 2)
- SummarizationAgent (OpenAI 연동)
- ContentCuratorAgent (중요도 랭킹)
- SchedulerAgent (매일 7시 자동 실행)
