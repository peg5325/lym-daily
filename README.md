# 임영웅 Daily 💙

> 엄마를 위해 만든 임영웅 일일 요약 서비스

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Development Status](https://img.shields.io/badge/status-in%20development-orange)]()
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)]()

## 📖 프로젝트 소개

**임영웅 Daily**는 가수 임영웅의 팬들을 위한 일일 뉴스 큐레이션 서비스입니다. 매일 아침 7시에 자동으로 뉴스를 수집하고, AI가 중요한 소식 3개를 선별하여 요약해 드립니다.

### 🎯 프로젝트 배경

매일 여러 사이트를 돌아다니며 임영웅 소식을 확인하시는 엄마를 위해 시작한 프로젝트입니다.
**"오늘 임영웅 소식이 뭐가 있었지?"** 라는 질문에 바로 답해주는 간단한 페이지를 만들어드리고 싶었습니다.

> 정보 확인 시간을 **30분 → 3분**으로 단축하는 것이 목표입니다.

---

## ✨ 주요 기능

- 🤖 **AI 기반 뉴스 자동 요약** - OpenAI GPT를 활용한 40자 이내 한글 요약
- 📰 **매일 자동 업데이트** - 매일 아침 7시에 자동으로 최신 뉴스 수집
- 🎯 **스마트 큐레이션** - 중요도 기반 알고리즘으로 상위 3개 뉴스 선정
- 📸 **화제의 미디어** - YouTube 최신 영상 및 화제의 사진 제공
- 📅 **스케줄 관리** - 이번 주 일정을 한눈에 확인
- 📱 **모바일 최적화** - 스크롤 없이 한 화면에서 모든 정보 확인

---

## 🏗️ 기술 스택

### Backend
- **Framework**: Spring Boot 2.7
- **Database**: MySQL + JPA
- **API Integration**:
  - Naver News API (뉴스 검색)
  - YouTube Data API v3 (영상 수집)
  - OpenAI API (GPT-3.5 Turbo)
- **Crawling**: Jsoup
- **Scheduling**: Spring Scheduler (@Scheduled)

### Frontend
- **Framework**: Vue.js 3 (Composition API)
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **State Management**: Vue Composables

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **Deployment**: AWS EC2 (t3.small)
- **Web Server**: Nginx
- **SSL**: Let's Encrypt

---

## 🎨 서브 에이전트 아키텍처

이 프로젝트는 **서브 에이전트 패턴**을 사용하여 각 책임을 명확히 분리합니다.

### Backend Agents

| Agent | 역할 | 주요 기능 |
|-------|------|----------|
| **NewsCollectorAgent** | 뉴스 수집 | 네이버 API 연동, 중복 제거 |
| **MediaCollectorAgent** | 미디어 수집 | YouTube API 연동, 공식 채널 우선순위 |
| **SummarizationAgent** | AI 요약 | OpenAI 요약, 중요도 점수 산정 |
| **ContentCuratorAgent** | 콘텐츠 큐레이션 | 상위 3개 선정, 중복 제거 |
| **SchedulerAgent** | 스케줄링 | 매일 7시 자동 실행, 재시도 로직 |
| **CacheAgent** | 캐싱 | API 응답 캐싱, 비용 절감 |

### Frontend Agents (Composables)

| Agent | 역할 | 주요 기능 |
|-------|------|----------|
| **DataFetchAgent** | API 호출 | 로딩/에러 상태 관리, 자동 재시도 |
| **DateNavigationAgent** | 날짜 관리 | 날짜 전환, 한글 포맷팅 |

### 데이터 플로우

```
매일 아침 7시
    ↓
SchedulerAgent 실행
    ↓
NewsCollectorAgent → 네이버 뉴스 10개 수집
    ↓
MediaCollectorAgent → YouTube 영상 5개 수집
    ↓
SummarizationAgent → OpenAI 요약 생성
    ↓
ContentCuratorAgent → 상위 3개 선정
    ↓
MySQL 저장 + 캐시 무효화
    ↓
사용자 방문 → 오늘의 요약 확인
```

> 자세한 내용은 [AGENTS.md](./AGENTS.md)를 참고하세요.

---

## 📊 개발 로드맵

### ✅ Phase 0: 설계 (완료)
- [x] PRD 작성
- [x] 서브 에이전트 아키텍처 설계
- [x] 개발 워크플로우 정립

### 🚧 Phase 1: Week 1 - 기본 구조 (진행 예정)
- [ ] Spring Boot 프로젝트 생성
- [ ] MySQL 데이터베이스 설계
- [ ] NewsCollectorAgent 구현
- [ ] 기본 REST API 구현

### 📅 Phase 2: Week 2 - AI 통합
- [ ] OpenAI API 연동
- [ ] SummarizationAgent 구현
- [ ] ContentCuratorAgent 구현
- [ ] SchedulerAgent 구현

### 📅 Phase 3: Week 3 - 미디어 & 프론트엔드
- [ ] MediaCollectorAgent 구현
- [ ] CacheAgent 구현
- [ ] Vue.js 프론트엔드 구현
- [ ] 반응형 디자인 적용

### 📅 Phase 4: Week 4 - 배포
- [ ] Docker 컨테이너화
- [ ] AWS EC2 배포
- [ ] HTTPS 설정
- [ ] 엄마 사용 테스트

**전체 진행률**: 5% (아키텍처 설계 완료)

---

## 📁 프로젝트 구조 (예정)

```
formom/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/formom/daily/
│   │   ├── agent/             # 서브 에이전트들
│   │   │   ├── collector/     # NewsCollectorAgent, MediaCollectorAgent
│   │   │   ├── ai/            # SummarizationAgent
│   │   │   ├── curator/       # ContentCuratorAgent
│   │   │   ├── scheduler/     # SchedulerAgent
│   │   │   └── cache/         # CacheAgent
│   │   ├── controller/        # REST API 컨트롤러
│   │   ├── service/           # 비즈니스 로직
│   │   ├── repository/        # JPA 리포지토리
│   │   └── entity/            # JPA 엔티티
│   └── src/main/resources/
│       └── application.yml    # 설정 파일
│
├── frontend/                  # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/        # Vue 컴포넌트
│   │   ├── composables/       # 프론트엔드 에이전트
│   │   ├── views/             # 페이지
│   │   └── App.vue
│   └── package.json
│
├── docker-compose.yml         # Docker 구성
├── prd.md                     # 프로젝트 요구사항 문서
├── TODO.md                    # 개발 할 일 목록
├── AGENTS.md                  # 서브 에이전트 상세 설계
├── CLAUDE.md                  # Claude Code 가이드
└── README.md                  # 이 파일
```

---

## 🚀 시작하기

### 필수 요구사항

- Java 11+
- Node.js 16+
- MySQL 8.0+
- Docker & Docker Compose (선택)

### API 키 발급

다음 API 키가 필요합니다:
- [Naver Developers](https://developers.naver.com/) - 뉴스 검색 API
- [Google Cloud Console](https://console.cloud.google.com/) - YouTube Data API v3
- [OpenAI Platform](https://platform.openai.com/) - GPT API

### 환경 변수 설정

`.env` 파일 생성:
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

### 설치 및 실행

> ⚠️ **주의**: 프로젝트가 현재 개발 중이므로 아래 명령어는 구현 완료 후 사용 가능합니다.

```bash
# 저장소 클론
git clone https://github.com/yourusername/formom.git
cd formom

# Backend 실행
cd backend
./gradlew bootRun

# Frontend 실행 (새 터미널)
cd frontend
npm install
npm run dev

# Docker로 실행 (권장)
docker-compose up -d
```

---

## 🎯 API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/today` | 오늘의 요약 (뉴스 3개, 미디어, 스케줄) |
| GET | `/api/date/{date}` | 특정 날짜의 요약 |
| GET | `/api/schedules/week` | 이번 주 스케줄 |
| POST | `/api/schedules` | 스케줄 등록 (관리자) |
| PUT | `/api/schedules/{id}` | 스케줄 수정 |
| DELETE | `/api/schedules/{id}` | 스케줄 삭제 |

---

## 👥 사용자 피드백

**주 사용자**: 개발자의 어머니 (60대, 임영웅 팬)

**주요 요구사항**:
- ✅ 글자 크기가 커야 함
- ✅ 복잡한 기능 없이 단순해야 함
- ✅ 모바일에서 스크롤 없이 모든 정보가 보여야 함
- ✅ 매일 아침 자동 업데이트

**예상 성과**:
- 정보 확인 시간: 30분 → 3분
- 중요 소식 놓침: 0건
- 일일 사용률: 100%

---

## 🔮 향후 개선 계획 (v2)

엄마의 피드백을 받은 후 우선순위에 따라 개발 예정:

- [ ] 카카오톡 알림 (새 소식 푸시)
- [ ] 화제의 영상 섹션 추가
- [ ] 지난 주/월 요약 보기
- [ ] 다크모드 지원
- [ ] 폰트 크기 조절 기능
- [ ] 북마크/스크랩 기능
- [ ] 관리자 페이지 (스케줄 등록 UI)

---

## 📝 개발 워크플로우

### 브랜치 전략
- `main`: 프로덕션 배포
- `develop`: 개발 브랜치
- `feature/기능명`: 기능 개발

### Pull Request 필수
모든 개발은 feature 브랜치에서 진행하고, PR을 통해 코드 리뷰 후 병합합니다.

```bash
# 새 기능 개발 시작
git checkout -b feature/news-collector
# 작업 후 커밋
git commit -m "feat: implement NewsCollectorAgent"
# GitHub에 푸시
git push -u origin feature/news-collector
# PR 생성 후 리뷰
```

### 커밋 컨벤션
- `feat:` 새로운 기능
- `fix:` 버그 수정
- `docs:` 문서 수정
- `refactor:` 리팩토링
- `test:` 테스트 코드
- `chore:` 빌드/설정 변경

---

## 💰 예상 운영 비용

| 항목 | 월 비용 | 비고 |
|------|---------|------|
| AWS EC2 (t3.small) | $10-15 | 프리티어 종료 후 |
| OpenAI API | $5-10 | 하루 10건 요약 기준 |
| 도메인 (선택) | $1 | .com 기준 |
| **총 예상 비용** | **$16-26** | 매우 저렴 |

💡 **절약 팁**:
- 프리티어 기간 중 EC2 무료
- OpenAI 무료 크레딧 활용
- 도메인 없이 IP로 접속 가능

---

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

---

## 🤝 기여하기

이 프로젝트는 개인 포트폴리오 프로젝트이지만, 개선 제안은 언제나 환영합니다!

1. 이 저장소를 Fork 하세요
2. 새 브랜치를 만드세요 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋하세요 (`git commit -m 'feat: add amazing feature'`)
4. 브랜치에 푸시하세요 (`git push origin feature/amazing-feature`)
5. Pull Request를 생성하세요

---

## 📧 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해 주세요.

---

<div align="center">

**Made with ❤️ for Mom**

[프로젝트 문서](./prd.md) • [에이전트 설계](./AGENTS.md) • [개발 가이드](./CLAUDE.md) • [할 일 목록](./TODO.md)

</div>
