# 임영웅 Daily - 개발 할 일 목록

## 📋 개발 시작 전 준비사항

### API 키 발급
- [x] 네이버 API 키 발급 (뉴스 검색용)
- [x] YouTube API 키 발급 (영상 수집용)
- [x] OpenAI API 키 발급 (뉴스 요약용)

### 프로젝트 설정
- [ ] GitHub 레포지토리 생성
- [ ] Git 브랜치 전략 설정 (main, develop 브랜치)
- [ ] 엄마와 인터뷰 (필요한 정보 파악)
- [x] 서브 에이전트 아키텍처 설계 (AGENTS.md)
- [x] Pull Request 기반 코드 리뷰 워크플로우 설정 (CLAUDE.md)

---

## Week 1: 기본 구조 ⚙️

### Backend
- [x] Spring Boot 프로젝트 생성
- [x] 에이전트 패키지 구조 생성 (agent/collector, agent/ai, agent/curator 등)
- [x] MySQL 연결 설정
- [x] 데이터베이스 테이블 생성
  - [x] news 테이블
  - [x] media 테이블
  - [x] schedule 테이블
- [x] NewsCollectorAgent 구현
  - [x] 네이버 뉴스 API 연동
  - [x] 기본 크롤링 로직 구현 (하루치 뉴스 가져오기)
  - [x] 중복 뉴스 필터링

### Frontend
- [x] Vue 3 프로젝트 생성
- [x] Tailwind CSS 설정
- [x] 메인 페이지 레이아웃 작성
- [x] 뉴스 카드 컴포넌트 작성
- [x] Backend API 연동
- [x] 실제 뉴스 데이터 표시
- [x] CORS 설정 추가

### Week 1 목표
✅ 네이버에서 뉴스 가져와서 화면에 보여주기 **(완료!)**

---

## Week 2: AI 요약 🤖

### Backend
- [x] SummarizationAgent 구현
  - [x] OpenAI API 연동
  - [x] 요약 프롬프트 작성 (40자 이내, 한글)
  - [x] 중요도 점수 산정 로직
  - [x] 배치 처리 지원
- [x] ContentCuratorAgent 구현
  - [x] 중요도 기반 랭킹 알고리즘
  - [x] 상위 3개 뉴스 선정 로직
  - [x] 중복 콘텐츠 제거
- [x] SchedulerAgent 구현
  - [x] 스케줄러 설정 (매일 7시 자동 실행)
  - [x] 전체 데이터 수집 플로우 조율
  - [x] 에러 핸들링 및 재시도 로직
- [x] REST API 구현
  - [x] GET /api/today - 오늘의 요약
  - [x] GET /api/date/{date} - 특정 날짜 요약
  - [x] POST /api/admin/collect - 수동 뉴스 수집

### Frontend
- [x] DataFetchAgent (Composable) 구현
  - [x] API 연동 (useDataFetch)
  - [x] 로딩 상태 처리
  - [x] 에러 처리 및 사용자 친화적 메시지
  - [x] 자동 재시도 로직
- [x] HomeView 업데이트
  - [x] TOP 3 뉴스 표시
  - [x] 한글 날짜 포맷
  - [x] AI 선정 안내 배너

### Week 2 목표
✅ AI가 요약한 오늘의 뉴스 3개 보여주기 **(완료!)**

---

## Week 3: 미디어 & 스케줄 📸📅

### Backend
- [x] MediaCollectorAgent 구현
  - [x] YouTube API 연동
  - [x] 최신 영상 3개 가져오기
  - [x] 썸네일 및 메타데이터 저장
  - [x] 공식 채널 우선순위 처리
  - [x] NEW 배지 기능 (오늘 올라온 영상 표시)
  - [x] VideoType 분류 (SHORTS vs REGULAR)
- [x] CacheAgent 구현
  - [x] 오늘의 요약 데이터 캐싱 (1시간)
  - [x] API 응답 캐싱
  - [x] 캐시 무효화 전략
- [x] 스케줄 CRUD API 구현
  - [x] POST /api/schedules - 스케줄 등록
  - [x] GET /api/schedules/week - 이번 주 스케줄 조회
  - [x] PUT /api/schedules/{id} - 스케줄 수정
  - [x] DELETE /api/schedules/{id} - 스케줄 삭제
- [x] Media API 구현
  - [x] GET /api/media/top3 - 최신 영상 TOP 3

### Frontend
- [x] DateNavigationAgent (Composable) 구현
  - [x] 어제/오늘 전환 버튼
  - [x] 날짜 포맷팅 (한글)
  - [x] URL 파라미터 동기화
- [x] YouTube 영상 섹션 구현
  - [x] MediaCard 컴포넌트
  - [x] NEW 배지 UI
  - [x] 공식 채널 링크
- [x] 스케줄 섹션 구현
  - [x] ScheduleCard 컴포넌트
  - [x] 이번 주 일정 표시
- [x] 반응형 디자인 적용 (모바일 최적화)
- [x] UI/UX 개선 (엄마 사용성 중심)

### Week 3 목표
✅ 완성된 MVP (모든 기능 동작) **(완료!)**

---

## Week 4: 배포 & 테스트 🚀

### 인프라 구축
- [ ] AWS EC2 인스턴스 생성 (t3.small)
- [ ] MySQL 설치 및 설정
- [ ] Nginx 설치 및 설정

### 배포
- [x] Backend Dockerfile 작성
- [x] Frontend Dockerfile 작성
- [x] Docker Compose 설정
- [x] 로컬 Docker Compose 테스트
- [x] 환경 변수 설정 (.env)

### 보안 & 도메인
- [ ] HTTPS 설정 (Let's Encrypt)
- [ ] 도메인 연결 (선택사항)
- [ ] 방화벽 설정

### 테스트
- [ ] 엄마 사용 테스트
- [ ] 피드백 수집
- [ ] 버그 수정
- [ ] 성능 최적화

### Week 4 목표
✅ 엄마가 실제로 사용 시작!

---

## 📝 완료 후 해야 할 일

### 문서화
- [x] README.md 작성
  - [x] 프로젝트 소개
  - [x] 기술 스택
  - [x] 주요 기능
  - [x] 서브 에이전트 아키텍처 설명
  - [ ] 실사용 스크린샷 (구현 후 추가)
  - [x] 설치 및 실행 방법
- [ ] API 문서 작성
- [x] 배포 가이드 작성 (DOCKER.md)
- [ ] 에이전트별 테스트 문서 작성

### 피드백 반영
- [ ] 매주 엄마에게 진행상황 공유
- [ ] 엄마 피드백 수집 및 반영
- [ ] 사용성 개선

### 포트폴리오
- [ ] 포트폴리오 사이트에 프로젝트 추가
- [ ] GitHub 프로필 업데이트
- [ ] LinkedIn에 프로젝트 공유
- [ ] 성과 지표 정리
  - 정보 확인 시간 단축 효과
  - 일일 사용률
  - 수집/요약되는 뉴스 수

---

## 🎯 향후 개선 아이디어 (v2)

> 엄마 피드백 후 우선순위에 따라 개발

- [ ] 카카오톡 알림 기능 (새 소식 알림)
- [ ] 화제의 영상 섹션
- [ ] 지난 주/월 요약 보기
- [ ] 다크모드 지원
- [ ] 폰트 크기 조절 기능
- [ ] 북마크/스크랩 기능
- [ ] 관리자 페이지 (스케줄 등록 UI)

---

## 📊 진행 상황 트래킹

- **전체 진행률**: 80%
- **현재 단계**: Week 4 배포 진행 중 - 로컬 Docker 테스트 완료, AWS 배포 대기
- **예상 완료일**: [시작일 + 4주]
- **최근 완료** (Docker 구축):
  - Backend Dockerfile 작성 (Multi-stage build, Java 17)
  - Frontend Dockerfile 작성 (Multi-stage build, Nginx)
  - Docker Compose 설정 (MySQL + Backend + Frontend)
  - 환경 변수 템플릿 생성 (.env.example)
  - DOCKER.md 배포 가이드 작성
  - 로컬 Docker Compose 전체 스택 테스트 완료

### 주차별 체크포인트
- Week 1: [x] **완료!** Backend + Frontend 기본 구조, 뉴스 표시 기능 동작
- Week 2: [x] **완료!** AI 요약 및 TOP 3 큐레이션 동작
- Week 3: [x] **완료!** MVP 완성 (미디어, 스케줄 기능)
- Week 4: [ ] 배포 완료

---

## 💡 개발 팁

### 커밋 컨벤션
- `feat:` 새로운 기능
- `fix:` 버그 수정
- `docs:` 문서 수정
- `style:` 코드 포맷팅
- `refactor:` 리팩토링
- `test:` 테스트 코드
- `chore:` 빌드/설정 변경

### 브랜치 전략
- `main`: 배포용
- `develop`: 개발용
- `feature/기능명`: 기능 개발

### 주의사항
- [ ] 커밋 메시지 깔끔하게 작성
- [ ] 매주 엄마에게 진행상황 공유
- [ ] README 작성하면서 진행
- [ ] 민감 정보 (.env) git에 올리지 않기
- [ ] API 키는 환경 변수로 관리
