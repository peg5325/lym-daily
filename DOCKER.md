# 🐳 Docker 배포 가이드

## 📋 사전 준비

### 1. Docker 설치
- [Docker Desktop](https://www.docker.com/products/docker-desktop) 설치 필요
- Docker Compose v3.8 이상 지원

### 2. 환경 변수 설정
프로젝트 루트에서 `.env` 파일 생성:

```bash
cp .env.example .env
```

`.env` 파일에 실제 API 키 입력:

```env
# Database
DB_PASSWORD=your_mysql_password
DB_NAME=formom
DB_USERNAME=root

# API Keys
NAVER_API_KEY=your_naver_client_id
NAVER_API_SECRET=your_naver_client_secret
YOUTUBE_API_KEY=your_youtube_api_key
OPENAI_API_KEY=your_openai_api_key

# Backend
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Frontend
VITE_API_BASE_URL=http://localhost:8080
```

## 🚀 빠른 시작

### 전체 스택 실행 (MySQL + Backend + Frontend)

```bash
# 빌드 및 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 종료
docker-compose down

# 볼륨까지 삭제 (데이터베이스 초기화)
docker-compose down -v
```

## 🔧 개별 서비스 실행

### Backend만 빌드
```bash
docker build -t formom-backend -f backend/Dockerfile backend/
```

### Frontend만 빌드
```bash
docker build -t formom-frontend --build-arg VITE_API_BASE_URL=http://localhost:8080 -f frontend/Dockerfile frontend/
```

## 📦 서비스 포트

- **Frontend**: http://localhost:80
- **Backend**: http://localhost:8080
- **MySQL**: localhost:3306

## 🔍 Health Check

각 서비스는 헬스 체크를 지원합니다:

```bash
# Backend health check
curl http://localhost:8080/actuator/health

# Frontend health check
curl http://localhost/health

# 서비스 상태 확인
docker-compose ps
```

## 🛠️ 개발 모드

개발 환경에서는 `docker-compose.dev.yml`을 함께 사용:

```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

개발 모드 특징:
- Frontend 포트: 5173
- Backend 디버그 포트: 5005
- 로그 볼륨 마운트

## 📝 문제 해결

### MySQL 연결 실패
```bash
# MySQL 컨테이너 로그 확인
docker-compose logs db

# MySQL 컨테이너 재시작
docker-compose restart db
```

### 빌드 캐시 초기화
```bash
# 모든 캐시 삭제 후 재빌드
docker-compose build --no-cache
```

### 볼륨 초기화 (데이터베이스 리셋)
```bash
docker-compose down -v
docker volume prune
```

## 🌐 프로덕션 배포 (AWS EC2)

### 1. EC2 인스턴스 준비
```bash
# Docker 설치
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 2. 코드 배포
```bash
# Git clone
git clone https://github.com/peg5325/lym-daily.git
cd lym-daily

# .env 파일 생성 및 설정
cp .env.example .env
nano .env  # API 키 입력
```

### 3. 실행
```bash
# Production 모드로 실행
SPRING_PROFILES_ACTIVE=prod VITE_API_BASE_URL=http://your-ec2-public-ip:8080 docker-compose up -d
```

### 4. Nginx 리버스 프록시 (선택)
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:80;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## 📊 모니터링

```bash
# 실시간 리소스 사용량
docker stats

# 컨테이너 로그
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

## 🔐 보안 체크리스트

- [ ] `.env` 파일은 절대 Git에 커밋하지 않기
- [ ] Production 환경에서는 강력한 DB 비밀번호 사용
- [ ] HTTPS 설정 (Let's Encrypt)
- [ ] 방화벽 설정 (AWS Security Group)
- [ ] 정기적인 백업 설정
