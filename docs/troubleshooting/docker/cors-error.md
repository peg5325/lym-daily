# Docker Compose 환경에서 CORS 에러 해결

## 문제 상황

Docker Compose로 전체 스택(`docker-compose up`)을 실행했을 때 브라우저 콘솔에서 다음과 같은 CORS 에러가 발생:

```
Access to fetch at 'http://localhost:8080/api/schedules/week' from origin 'http://localhost'
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
GET http://localhost:8080/api/schedules/week net::ERR_FAILED 403 (Forbidden)
```

### 발생 원인

1. **Different Origin 문제**
   - Frontend: `http://localhost` (포트 80)
   - Backend: `http://localhost:8080` (포트 8080)
   - 브라우저가 다른 origin 간의 통신을 CORS 정책으로 차단

2. **환경 변수 전달 실패**
   - Frontend 빌드 시 `VITE_API_BASE_URL` 환경 변수가 제대로 전달되지 않음
   - `.env.production` 파일의 빈 값이 Vite에 의해 무시됨
   - 결과적으로 Frontend가 여전히 `http://localhost:8080`으로 API 호출

---

## 해결 방법

### 1. Nginx 프록시 설정 추가

**파일**: `frontend/nginx.conf`

```nginx
# Proxy API requests to backend
location /api/ {
    proxy_pass http://backend:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_cache_bypass $http_upgrade;
}
```

**설명**: Nginx가 `/api` 경로로 들어오는 요청을 Docker 네트워크 내부의 `backend:8080`으로 프록시합니다.

---

### 2. Frontend API Base URL 수정

**파일**: `frontend/src/views/HomeView.vue`

**변경 전**:
```typescript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''
```

**변경 후**:
```typescript
// 프로덕션(Docker) 환경: 빈 문자열 - Nginx가 /api를 backend로 프록시
// 개발 환경: http://localhost:8080
const API_BASE_URL = import.meta.env.PROD
  ? ''
  : (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080')
```

**설명**:
- `import.meta.env.PROD`를 사용하여 프로덕션 빌드 시 무조건 빈 문자열 사용
- 개발 환경에서는 `http://localhost:8080` 사용 (로컬에서 Backend와 직접 통신)

---

### 3. Dockerfile ARG 기본값 설정

**파일**: `frontend/Dockerfile`

```dockerfile
# Build argument for API URL (기본값: 빈 문자열 - Nginx 프록시 사용)
ARG VITE_API_BASE_URL=""
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
```

**설명**: ARG에 기본값을 명시적으로 설정하여 환경 변수가 없어도 빈 문자열 사용

---

### 4. Production 환경 변수 파일 생성

**파일**: `frontend/.env.production`

```bash
# Production 환경 변수
# Docker 환경에서는 Nginx가 /api를 backend로 프록시하므로 빈 문자열 사용
VITE_API_BASE_URL=
```

**설명**: Vite가 프로덕션 빌드 시 이 파일을 읽도록 설정 (하지만 빈 값은 무시될 수 있음)

---

### 5. docker-compose.yml 수정

**파일**: `docker-compose.yml`

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
    args:
      # 빈 문자열로 설정 (Nginx가 /api를 backend로 프록시)
      VITE_API_BASE_URL: ${VITE_API_BASE_URL:-}
```

---

## 작동 원리

### 변경 전 (CORS 에러 발생)
```
Browser → http://localhost (Frontend/Nginx)
         → http://localhost:8080/api/... (Backend) ❌ CORS 에러
```

### 변경 후 (Same Origin)
```
Browser → http://localhost/api/... (Frontend/Nginx)
         → Nginx Proxy
         → http://backend:8080/api/... (Backend) ✅ 성공
```

**핵심**:
- 브라우저는 `http://localhost/api/...`로만 요청 (Same Origin)
- Nginx가 내부적으로 `backend:8080`으로 프록시
- Docker 네트워크 내부 통신이므로 CORS 문제 없음

---

## 재빌드 및 테스트

```bash
# 1. 기존 컨테이너 중지 및 삭제
docker-compose down

# 2. Frontend 이미지 재빌드 (캐시 무효화)
docker-compose build --no-cache frontend

# 3. 전체 스택 재시작
docker-compose up -d

# 4. 빌드 확인 (localhost:8080이 포함되지 않아야 함)
docker exec formom-frontend grep -o "localhost:8080" /usr/share/nginx/html/assets/*.js \
  || echo "✅ SUCCESS: No localhost:8080 found!"
```

---

## 브라우저에서 확인

1. **브라우저 하드 리프레시** (필수!)
   - Chrome/Edge: `Ctrl + Shift + R` (Windows) / `Cmd + Shift + R` (Mac)
   - 또는 시크릿 모드로 `http://localhost` 접속

2. **개발자 도구 확인** (F12)
   - Network 탭에서 API 호출이 `/api/...`로 되는지 확인
   - ❌ `http://localhost:8080/api/...` (잘못됨)
   - ✅ `/api/...` 또는 `http://localhost/api/...` (정상)

3. **CORS 에러 없이 정상 작동 확인**
   - 콘솔에 CORS 관련 에러가 없어야 함
   - API 데이터가 정상적으로 로드되어야 함

---

## 추가 참고 사항

### 개발 환경과 프로덕션 환경의 차이

| 환경 | API Base URL | 작동 방식 |
|------|-------------|----------|
| **개발** (`npm run dev`) | `http://localhost:8080` | Frontend가 Backend에 직접 요청 |
| **프로덕션** (Docker) | `` (빈 문자열) | Nginx가 `/api`를 `backend:8080`으로 프록시 |

### 트러블슈팅 체크리스트

- [ ] `frontend/nginx.conf`에 `/api` 프록시 설정이 있는가?
- [ ] `frontend/src/views/HomeView.vue`에서 `import.meta.env.PROD` 체크가 있는가?
- [ ] `frontend/Dockerfile`에 `ARG VITE_API_BASE_URL=""`이 설정되어 있는가?
- [ ] Frontend 이미지를 `--no-cache`로 재빌드했는가?
- [ ] 브라우저 하드 리프레시를 했는가?

---

## 관련 파일

- `frontend/nginx.conf:18-29` - Nginx 프록시 설정
- `frontend/src/views/HomeView.vue:37-40` - API Base URL 설정
- `frontend/Dockerfile:15-17` - 환경 변수 ARG 설정
- `frontend/.env.production` - 프로덕션 환경 변수
- `docker-compose.yml:69-71` - Frontend 빌드 ARG 전달
