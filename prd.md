# 임영웅 Daily - 엄마를 위한 일일 요약 서비스 PRD

## 문서 정보
- **작성일**: 2024.11.17
- **프로젝트 성격**: 개인 포트폴리오 + 실사용 프로젝트
- **타겟 사용자**: 엄마 (임영웅 팬)
- **개발 기간**: 3-4주 (MVP)

---

## 1. 프로젝트 개요

### 1.1 왜 만드나요?
엄마는 매일 임영웅 관련 뉴스를 여러 사이트에서 찾아보시는데, 시간이 많이 걸리고 중요한 소식을 놓치는 경우가 있어요. 
**"오늘 임영웅 소식이 뭐가 있었지?"** 라는 질문에 바로 답해주는 간단한 페이지를 만들어드리고 싶어요!

### 1.2 핵심 가치
- ✅ **간편함**: 복잡한 기능 없이, 딱 필요한 정보만
- ✅ **매일 업데이트**: 아침에 들어오면 오늘의 소식이 준비되어 있음
- ✅ **한눈에 파악**: 스크롤 없이 메인 화면에서 주요 정보 확인

### 1.3 포트폴리오 포인트
1. **실제 사용자**가 있는 프로젝트 (엄마의 피드백 기반 개선)
2. **백엔드 역량**: 크롤링, 스케줄링, REST API, JPA
3. **AI 통합**: OpenAI API 활용한 자동 요약
4. **전체 사이클**: 기획 → 개발 → 배포 → 운영

---

## 2. MVP (Minimum Viable Product)

### 2.1 꼭 필요한 기능만!

#### 🎯 메인 페이지 (단일 페이지)
```
┌─────────────────────────────────────┐
│  임영웅 Daily 💙                     │
│  2024년 11월 17일 일요일             │
├─────────────────────────────────────┤
│                                      │
│  📰 오늘의 주요 뉴스 (3개)           │
│  ┌────────────────────────────────┐ │
│  │ [이미지] 제목                   │ │
│  │ 한 줄 요약...                   │ │
│  │ [원문 보기]                     │ │
│  └────────────────────────────────┘ │
│                                      │
│  📸 화제의 사진 (3개)                │
│  [사진1] [사진2] [사진3]            │
│                                      │
│  📅 이번 주 스케줄                   │
│  • 11/18(월) 라디오스타 출연        │
│  • 11/20(수) 콘서트 리허설          │
│                                      │
│  [어제 보기] 버튼                   │
└─────────────────────────────────────┘
```

### 2.2 수집 소스 (최소한으로)
- **네이버 뉴스**: 임영웅 키워드 검색 (API 사용)
- **유튜브**: 공식 채널 최신 영상 (API 사용)
- **스케줄**: 수동 등록 (크롤링 X, 직접 입력)

### 2.3 데이터 플로우 (심플하게)
```
매일 아침 7시
  ↓
네이버 뉴스 크롤링 (10개)
  ↓
AI가 중요한 뉴스 3개 선정 + 요약
  ↓
DB 저장
  ↓
엄마가 페이지 열면 오늘 요약 보여줌
```

---

## 3. 기술 스택 (단순하게)

### 3.1 Backend
```
Spring Boot 2.7
└── JPA (MySQL)
└── Jsoup (크롤링)
└── OpenAI API (요약)
└── Spring Scheduler (매일 7시 실행)
```

### 3.2 Frontend
```
Vue.js 3
└── Tailwind CSS (깔끔한 UI)
└── Axios (API 호출)
```

### 3.3 Database (심플 설계)
```sql
-- 뉴스 테이블
CREATE TABLE news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500),
    summary VARCHAR(200),        -- AI 요약
    thumbnail_url VARCHAR(1000),
    source_url VARCHAR(1000),
    published_date DATE,
    created_at TIMESTAMP
);

-- 미디어 테이블
CREATE TABLE media (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500),
    thumbnail_url VARCHAR(1000),
    source_url VARCHAR(1000),
    view_count INT,
    published_date DATE,
    created_at TIMESTAMP
);

-- 스케줄 테이블 (수동 입력)
CREATE TABLE schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500),
    schedule_date DATE,
    schedule_time VARCHAR(10),
    location VARCHAR(200)
);
```

### 3.4 배포
```
AWS EC2 t3.small (프리티어 or 저렴한 인스턴스)
└── Docker (Backend + Frontend)
└── MySQL (같은 인스턴스에 설치)
└── Nginx (프론트엔드 서빙)
```

---

## 4. 개발 계획 (3-4주)

### Week 1: 기본 구조 ⚙️
**Backend**
- [ ] Spring Boot 프로젝트 생성
- [ ] MySQL 연결 및 테이블 생성
- [ ] 네이버 뉴스 API 연동
- [ ] 기본 크롤링 로직 (하루치 뉴스 가져오기)

**Frontend**
- [ ] Vue 프로젝트 생성
- [ ] 메인 페이지 레이아웃
- [ ] 뉴스 카드 컴포넌트

**Goal**: 네이버에서 뉴스 가져와서 화면에 보여주기

---

### Week 2: AI 요약 🤖
**Backend**
- [ ] OpenAI API 연동
- [ ] 뉴스 요약 기능 구현
- [ ] 스케줄러 설정 (매일 7시 자동 실행)
- [ ] REST API 구현
  - `GET /api/today` - 오늘의 요약
  - `GET /api/date/{date}` - 특정 날짜 요약

**Frontend**
- [ ] API 연동
- [ ] 로딩 상태 처리
- [ ] 에러 처리

**Goal**: AI가 요약한 오늘의 뉴스 3개 보여주기

---

### Week 3: 미디어 & 스케줄 📸📅
**Backend**
- [ ] YouTube API 연동 (최신 영상 3개)
- [ ] 스케줄 CRUD API
  - 관리자가 직접 입력할 수 있도록

**Frontend**
- [ ] 화제의 사진 섹션
- [ ] 스케줄 섹션
- [ ] 어제/오늘 전환 버튼
- [ ] 반응형 디자인 (모바일 최적화)

**Goal**: 완성된 MVP

---

### Week 4: 배포 & 테스트 🚀
- [ ] AWS EC2 인스턴스 생성
- [ ] Docker로 빌드 & 배포
- [ ] 도메인 연결 (선택사항)
- [ ] HTTPS 설정 (Let's Encrypt)
- [ ] 엄마 테스트 및 피드백
- [ ] 버그 수정

**Goal**: 엄마가 실제로 사용 시작!

---

## 5. 핵심 코드 예시

### 5.1 뉴스 크롤링 + AI 요약
```java
@Service
public class NewsService {
    
    @Scheduled(cron = "0 0 7 * * *")  // 매일 7시
    public void collectDailyNews() {
        // 1. 네이버 뉴스 검색
        List<News> rawNews = naverApiClient.search("임영웅");
        
        // 2. AI로 중요 뉴스 선정 및 요약
        List<News> topNews = rawNews.stream()
            .limit(5)  // 상위 5개만
            .map(news -> {
                String summary = openAiService.summarize(news.getContent());
                news.setSummary(summary);
                return news;
            })
            .limit(3)  // 최종 3개
            .collect(Collectors.toList());
        
        // 3. DB 저장
        newsRepository.saveAll(topNews);
    }
}
```

### 5.2 간단한 AI 요약 프롬프트
```java
public String summarize(String content) {
    String prompt = String.format(
        "다음 뉴스를 40자 이내로 요약해주세요. 핵심만 간단하게:\n\n%s",
        content.substring(0, Math.min(1000, content.length()))
    );
    
    return openAiClient.complete(prompt);
}
```

### 5.3 오늘의 요약 API
```java
@RestController
@RequestMapping("/api")
public class SummaryController {
    
    @GetMapping("/today")
    public TodayResponse getTodaySummary() {
        LocalDate today = LocalDate.now();
        
        return TodayResponse.builder()
            .date(today)
            .topNews(newsRepository.findTop3ByPublishedDate(today))
            .topMedia(mediaRepository.findTop3ByPublishedDate(today))
            .schedules(scheduleRepository.findByWeek(today))
            .build();
    }
}
```

### 5.4 Frontend 메인 화면
```vue
<template>
  <div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold text-center mb-4">
      임영웅 Daily 💙
    </h1>
    <p class="text-center text-gray-600 mb-8">
      {{ formatDate(today) }}
    </p>

    <!-- 오늘의 뉴스 -->
    <section class="mb-8">
      <h2 class="text-2xl font-bold mb-4">📰 오늘의 주요 뉴스</h2>
      <div v-for="news in topNews" :key="news.id" class="card mb-4">
        <img :src="news.thumbnailUrl" class="w-full h-48 object-cover" />
        <h3 class="text-xl font-bold mt-2">{{ news.title }}</h3>
        <p class="text-gray-600">{{ news.summary }}</p>
        <a :href="news.sourceUrl" target="_blank" class="btn-primary">
          원문 보기
        </a>
      </div>
    </section>

    <!-- 화제의 사진 -->
    <section class="mb-8">
      <h2 class="text-2xl font-bold mb-4">📸 화제의 사진</h2>
      <div class="grid grid-cols-3 gap-4">
        <img v-for="media in topMedia" :key="media.id" 
             :src="media.thumbnailUrl" 
             class="w-full h-40 object-cover rounded" />
      </div>
    </section>

    <!-- 이번 주 스케줄 -->
    <section>
      <h2 class="text-2xl font-bold mb-4">📅 이번 주 스케줄</h2>
      <ul class="space-y-2">
        <li v-for="schedule in schedules" :key="schedule.id" class="flex">
          <span class="font-bold mr-2">{{ schedule.scheduleDate }}</span>
          <span>{{ schedule.title }}</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const topNews = ref([]);
const topMedia = ref([]);
const schedules = ref([]);
const today = ref(new Date());

onMounted(async () => {
  const response = await axios.get('/api/today');
  topNews.value = response.data.topNews;
  topMedia.value = response.data.topMedia;
  schedules.value = response.data.schedules;
});
</script>
```

---

## 6. 예상 비용 (매우 저렴)

| 항목 | 월 비용 | 비고 |
|------|---------|------|
| AWS EC2 t3.small | $10-15 | 프리티어 종료 후 |
| OpenAI API | $5-10 | 하루 10건 요약 기준 |
| 도메인 (선택) | $1 | .com 기준 |
| **총 예상 비용** | **$16-26** | 매우 저렴! |

💡 **절약 팁**: 
- 프리티어 기간 중이면 EC2 무료
- OpenAI 무료 크레딧 활용
- 도메인 없이 IP로 접속해도 OK

---

## 7. 향후 개선 아이디어 (엄마 피드백 후)

### 나중에 추가할 수 있는 것들
- [ ] 카카오톡 알림 (새 소식 있을 때)
- [ ] 화제의 영상 섹션
- [ ] 지난 주/월 요약 보기
- [ ] 다크모드
- [ ] 폰트 크기 조절 (보기 편하게)
- [ ] 북마크 기능

---

## 8. 포트폴리오 어필 포인트

### 8.1 기술 스택
✅ **Backend**: Spring Boot, JPA, REST API, 스케줄링  
✅ **Frontend**: Vue.js 3, Tailwind CSS, 반응형  
✅ **AI 통합**: OpenAI API  
✅ **크롤링**: Jsoup, API 연동  
✅ **DevOps**: Docker, AWS EC2, Nginx  

### 8.2 프로젝트 특징
✅ **실사용자 중심**: 엄마의 니즈를 정확히 파악하고 해결  
✅ **완성도**: 기획-개발-배포-운영까지 전 과정  
✅ **기술 응용**: 여러 기술을 실제 문제 해결에 활용  
✅ **유지보수 경험**: 실사용자 피드백 기반 개선  

### 8.3 README.md에 쓸 내용
```markdown
# 임영웅 Daily 💙

> 엄마를 위해 만든 임영웅 일일 요약 서비스

## 프로젝트 배경
매일 여러 사이트를 돌아다니며 임영웅 소식을 확인하시는 엄마를 위해,
오늘의 주요 소식을 한 페이지에서 볼 수 있도록 만들었습니다.

## 주요 기능
- 🤖 AI 기반 뉴스 자동 요약
- 📸 화제의 사진/영상 큐레이션
- 📅 이번 주 스케줄 한눈에 보기
- 📱 모바일 최적화

## 기술 스택
- Backend: Spring Boot, JPA, MySQL
- Frontend: Vue.js 3, Tailwind CSS
- AI: OpenAI GPT-3.5
- Infra: Docker, AWS EC2

## 실제 사용 스크린샷
[엄마가 사용하는 화면 캡처]

## 성과
- 실사용자(엄마) 일일 이용
- 정보 확인 시간 30분 → 3분으로 단축
- 중요 소식 놓치는 일 0건
```

---

## 9. 체크리스트

### 개발 시작 전
- [ ] 엄마와 인터뷰 (어떤 정보가 가장 필요한지)
- [ ] 네이버 API 키 발급
- [ ] YouTube API 키 발급
- [ ] OpenAI API 키 발급
- [ ] GitHub 레포지토리 생성

### 개발 중
- [ ] 매주 엄마에게 진행상황 공유
- [ ] 커밋 메시지 깔끔하게
- [ ] README 작성하면서 진행

### 완료 후
- [ ] 엄마 사용 테스트
- [ ] 피드백 반영
- [ ] 포트폴리오 사이트에 추가
- [ ] LinkedIn/GitHub에 공유

---

## 10. FAQ

**Q: 크롤링이 불법 아닌가요?**  
A: 네이버/유튜브 공식 API를 사용하고, 출처를 명시하며, 요약만 제공하기 때문에 문제없어요.

**Q: OpenAI API 비용이 부담되면?**  
A: 하루 10건 정도면 월 $5-10 정도로 매우 저렴해요. 초기에는 무료 크레딧도 있습니다.

**Q: 배포가 어려우면?**  
A: AWS 프리티어로 시작하면 1년간 무료고, 이후에도 월 $15 정도면 충분해요.

**Q: Vue.js 대신 React 써도 되나요?**  
A: 당연히요! 본인이 편한 프레임워크 사용하면 됩니다.

**Q: 혼자 개발 가능한가요?**  
A: 네! 기능이 단순해서 3-4주면 충분히 완성 가능합니다.

---

## 결론

이 프로젝트는:
- 🎯 **실용적**: 엄마가 실제로 매일 사용
- 💼 **포트폴리오**: 기술력을 보여줄 수 있는 완성도
- 💰 **저비용**: 월 2만원 이하로 운영 가능
- ⏰ **빠른 개발**: 3-4주면 MVP 완성

**가장 중요한 건**: 엄마가 좋아하실 거라는 확신! 😊

엄마의 피드백을 받으면서 조금씩 개선해나가는 것이 
이 프로젝트의 가장 큰 매력이에요.

화이팅! 🚀
