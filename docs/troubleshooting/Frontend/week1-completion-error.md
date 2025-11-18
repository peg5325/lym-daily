# 버그 해결 리포트

## Week 1 완료 후 Frontend 에러 (2025-11-18)

### 문제 증상
```
[plugin:vite:import-analysis] Failed to parse source for import analysis
because the content contains invalid JS syntax.
Install @vitejs/plugin-vue to handle .vue files.
/Users/eongyupark/Workspace/formom/frontend/src/App.vue:3:10
```

- **발생 시점**: Week 1 완료 후 브라우저 접속 시
- **영향 범위**: Frontend 전체 (Vue 파일 파싱 불가)
- **심각도**: Critical (서비스 중단)

### 원인 분석

1. **여러 개의 백그라운드 dev 서버 실행**
   - 4개 이상의 `npm run dev` 프로세스가 동시 실행 중
   - 서로 다른 포트/캐시 충돌 발생

2. **Vite 캐시 손상**
   - `.vite` 캐시 디렉토리에 오래된 설정 저장됨
   - vite.config.ts 변경이 감지되었으나 제대로 reload되지 않음
   - 손상된 캐시로 인해 `@vitejs/plugin-vue` 플러그인 인식 실패

3. **추가 확인된 문제**
   - CSS import 순서 경고도 함께 발생
   - node_modules 상태 불일치 가능성

### 해결 과정

#### 1단계: 백그라운드 프로세스 종료
```bash
# 실행 중인 모든 dev 서버 확인
ps aux | grep "npm run dev"

# 백그라운드 셸 종료
# Shell IDs: 8661e4, 4ac6c2, 94e724, 905a87
```

#### 2단계: 캐시 및 의존성 완전 정리
```bash
cd frontend
rm -rf node_modules .vite dist package-lock.json
```

#### 3단계: 의존성 재설치
```bash
npm install --legacy-peer-deps
```
- Node.js 18.15.0 호환성을 위해 `--legacy-peer-deps` 플래그 사용
- 469개 패키지 설치 완료
- EBADENGINE 경고는 예상된 것 (Node 18 사용)

#### 4단계: 개발 서버 재시작
```bash
npm run dev
```
- Vite v5.4.21 정상 시작 (710ms)
- http://localhost:5173/ 정상 접속
- 에러 메시지 없음

### 결과

✅ **문제 완전 해결**
- Vue 파일 파싱 정상 작동
- HMR (Hot Module Replacement) 정상 작동
- 모든 컴포넌트 정상 렌더링

### 예방 조치

1. **개발 서버 관리**
   - 작업 시작 전 기존 dev 서버 확인 및 종료
   - `ps aux | grep "npm run dev"` 로 중복 실행 확인

2. **캐시 문제 발생 시 즉시 조치**
   - Vite 에러 발생 시 우선 `.vite` 폴더 삭제 시도
   - vite.config.ts 변경 후에는 서버 완전 재시작

3. **Clean Install 절차**
   ```bash
   rm -rf node_modules .vite dist package-lock.json
   npm install --legacy-peer-deps
   npm run dev
   ```

### 관련 이슈

- Node.js 18.15.0 사용으로 인한 패키지 호환성 경고 (정상)
- Tailwind CSS v3.4.18 사용 (v4는 PostCSS 플러그인 변경으로 미사용)
- Vite v5.4.0 사용 (v7은 Node 20+ 필요)

### 참고사항

이 문제는 개발 환경 관리 이슈로, 코드 자체에는 문제가 없었습니다.
향후 유사한 문제 발생 시 위 "예방 조치"의 Clean Install 절차를 우선 시도하는 것을 권장합니다.

---

**작성일**: 2025-11-18
**해결 시간**: 약 10분
**영향받은 파일**: 없음 (환경 설정 문제)
