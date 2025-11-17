# 임영웅 Daily - 개발 할 일 목록

## 📋 개발 시작 전 준비사항

### API 키 발급
- [ ] 네이버 API 키 발급 (뉴스 검색용)
- [ ] YouTube API 키 발급 (영상 수집용)
- [ ] OpenAI API 키 발급 (뉴스 요약용)

### 프로젝트 설정
- [ ] GitHub 레포지토리 생성
- [ ] Git 브랜치 전략 설정 (main, develop 브랜치)
- [ ] 엄마와 인터뷰 (필요한 정보 파악)
- [x] 서브 에이전트 아키텍처 설계 (AGENTS.md)
- [x] Pull Request 기반 코드 리뷰 워크플로우 설정 (CLAUDE.md)

---

## Week 1: 기본 구조 ⚙️

### Backend
- [ ] Spring Boot 프로젝트 생성
- [ ] 에이전트 패키지 구조 생성 (agent/collector, agent/ai, agent/curator 등)
- [ ] MySQL 연결 설정
- [ ] 데이터베이스 테이블 생성
  - [ ] news 테이블
  - [ ] media 테이블
  - [ ] schedule 테이블
- [ ] NewsCollectorAgent 구현
  - [ ] 네이버 뉴스 API 연동
  - [ ] 기본 크롤링 로직 구현 (하루치 뉴스 가져오기)
  - [ ] 중복 뉴스 필터링

### Frontend
- [ ] Vue 3 프로젝트 생성
- [ ] Tailwind CSS 설정
- [ ] 메인 페이지 레이아웃 작성
- [ ] 뉴스 카드 컴포넌트 작성

### Week 1 목표
✅ 네이버에서 뉴스 가져와서 화면에 보여주기

---

## Week 2: AI 요약 🤖

### Backend
- [ ] SummarizationAgent 구현
  - [ ] OpenAI API 연동
  - [ ] 요약 프롬프트 작성 (40자 이내, 한글)
  - [ ] 중요도 점수 산정 로직
  - [ ] 배치 처리 지원
- [ ] ContentCuratorAgent 구현
  - [ ] 중요도 기반 랭킹 알고리즘
  - [ ] 상위 3개 뉴스 선정 로직
  - [ ] 중복 콘텐츠 제거
- [ ] SchedulerAgent 구현
  - [ ] 스케줄러 설정 (매일 7시 자동 실행)
  - [ ] 전체 데이터 수집 플로우 조율
  - [ ] 에러 핸들링 및 재시도 로직
- [ ] REST API 구현
  - [ ] GET /api/today - 오늘의 요약
  - [ ] GET /api/date/{date} - 특정 날짜 요약

### Frontend
- [ ] DataFetchAgent (Composable) 구현
  - [ ] API 연동 (Axios 설정)
  - [ ] 로딩 상태 처리
  - [ ] 에러 처리 및 사용자 친화적 메시지
  - [ ] 자동 재시도 로직

### Week 2 목표
✅ AI가 요약한 오늘의 뉴스 3개 보여주기

---

## Week 3: 미디어 & 스케줄 📸📅

### Backend
- [ ] MediaCollectorAgent 구현
  - [ ] YouTube API 연동
  - [ ] 최신 영상 3개 가져오기
  - [ ] 썸네일 및 메타데이터 저장
  - [ ] 공식 채널 우선순위 처리
- [ ] CacheAgent 구현
  - [ ] 오늘의 요약 데이터 캐싱 (1시간)
  - [ ] API 응답 캐싱
  - [ ] 캐시 무효화 전략
- [ ] 스케줄 CRUD API 구현
  - [ ] POST /api/schedules - 스케줄 등록
  - [ ] GET /api/schedules/week - 이번 주 스케줄 조회
  - [ ] PUT /api/schedules/{id} - 스케줄 수정
  - [ ] DELETE /api/schedules/{id} - 스케줄 삭제

### Frontend
- [ ] DateNavigationAgent (Composable) 구현
  - [ ] 어제/오늘 전환 버튼
  - [ ] 날짜 포맷팅 (한글)
  - [ ] URL 파라미터 동기화
- [ ] 화제의 사진 섹션 구현
- [ ] 스케줄 섹션 구현
- [ ] 반응형 디자인 적용 (모바일 최적화)
- [ ] UI/UX 개선 (엄마 사용성 중심)

### Week 3 목표
✅ 완성된 MVP (모든 기능 동작)

---

## Week 4: 배포 & 테스트 🚀

### 인프라 구축
- [ ] AWS EC2 인스턴스 생성 (t3.small)
- [ ] MySQL 설치 및 설정
- [ ] Nginx 설치 및 설정

### 배포
- [ ] Backend Dockerfile 작성
- [ ] Frontend Dockerfile 작성
- [ ] Docker Compose 설정
- [ ] 빌드 및 배포 스크립트 작성
- [ ] 환경 변수 설정 (.env)

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
- [ ] 배포 가이드 작성
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

- **전체 진행률**: 10%
- **현재 단계**: 문서화 완료, 개발 시작 준비
- **예상 완료일**: [시작일 + 4주]
- **최근 완료**:
  - 서브 에이전트 설계 (AGENTS.md, CLAUDE.md)
  - Pull Request 워크플로우 설정
  - README.md 작성 완료

### 주차별 체크포인트
- Week 1: [ ] 기본 구조 완성
- Week 2: [ ] AI 요약 동작
- Week 3: [ ] MVP 완성
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
