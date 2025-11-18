# Week 1 Backend 설정 트러블슈팅

> 날짜: 2025-11-17
> 작업: Week 1 Backend 초기 설정 및 네이버 API 연동
> 결과: ✅ 성공

## 발생한 문제들

### 1. Gradle Wrapper 없음

**문제**:
```bash
./gradlew build
# 오류: no such file or directory: ./gradlew
```

**원인**:
- Spring Boot 프로젝트를 생성했지만 Gradle wrapper 파일들이 없었음
- `gradle wrapper` 명령어를 실행하려 했으나 Gradle이 시스템에 설치되지 않음

**해결**:
- IntelliJ IDEA에서 프로젝트를 열면 자동으로 Gradle wrapper 생성됨
- `backend/gradlew`, `backend/gradle/` 자동 생성 완료

**재발 방지**:
- IntelliJ로 프로젝트를 먼저 열어서 Gradle wrapper를 생성하거나
- Gradle을 시스템에 설치 (`brew install gradle`)

---

### 2. MySQL Public Key Retrieval 오류

**문제**:
```
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed
```

**원인**:
- MySQL 8.0은 기본적으로 `caching_sha2_password` 인증 방식 사용
- JDBC 드라이버가 서버의 공개 키를 가져오려 할 때 허용되지 않음

**해결**:
`application.yml`의 JDBC URL에 `allowPublicKeyRetrieval=true` 파라미터 추가:

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/formom?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
```

**참고**:
- 프로덕션 환경에서는 SSL 사용 권장 (`useSSL=true`)
- 개발 환경에서는 편의를 위해 `useSSL=false` 사용

---

### 3. MySQL Access Denied (첫 번째 시도)

**문제**:
```
java.sql.SQLException: Access denied for user 'root'@'192.168.65.1' (using password: YES)
```

**원인**:
- Docker MySQL 컨테이너는 기본적으로 `root@localhost`만 허용
- Spring Boot 애플리케이션은 Docker 외부에서 실행되므로 `192.168.65.1`(Docker Desktop for Mac의 호스트 IP)에서 접속
- MySQL이 원격 접속으로 인식하여 거부

**해결**:
MySQL 컨테이너에 접속하여 모든 호스트에서 root 접근 허용:

```bash
docker exec formom-mysql mysql -uroot -pformom1234 -e \
  "CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'formom1234'; \
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; \
   FLUSH PRIVILEGES;"
```

**확인**:
```bash
docker exec formom-mysql mysql -uroot -pformom1234 -e \
  "SELECT user, host FROM mysql.user WHERE user='root';"
```

결과:
```
user  host
root  %
root  localhost
```

---

### 4. MySQL Access Denied (두 번째 시도) - 진짜 원인

**문제**:
위 해결 후에도 동일한 오류 계속 발생:
```
java.sql.SQLException: Access denied for user 'root'@'192.168.65.1' (using password: YES)
```

**원인 분석**:
- **IntelliJ는 `.env` 파일을 자동으로 읽지 않음**
- `application.yml`의 환경 변수 기본값이 잘못됨:
  ```yaml
  password: ${DB_PASSWORD:password}  # ← 실제 비밀번호는 'formom1234'
  ```
- 환경 변수가 설정되지 않아 기본값 `password`로 접속 시도
- 실제 MySQL 비밀번호는 `formom1234`이므로 인증 실패

**해결**:
`application.yml`의 환경 변수 기본값을 실제 값으로 수정:

```yaml
datasource:
  url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:formom}?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:formom1234}  # ← 기본값 수정
  driver-class-name: com.mysql.cj.jdbc.Driver

# 네이버 API도 동일하게 기본값 추가
naver:
  api:
    client-id: ${NAVER_API_KEY:TQh1Q4MTNXgLS62k5tUU}
    client-secret: ${NAVER_API_SECRET:euywXtaoXf}
```

**교훈**:
- Spring Boot는 `.env` 파일을 자동으로 읽지 않음
- 환경 변수를 사용하려면:
  1. IntelliJ Run Configuration에서 직접 설정하거나
  2. `spring-dotenv` 라이브러리 추가하거나
  3. `application.yml`의 기본값을 개발 환경에 맞게 설정

---

## 최종 성공 구성

### Docker Compose (docker-compose.yml)
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: formom-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: formom1234
      MYSQL_DATABASE: formom
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-pformom1234"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql-data:
    driver: local
```

### Application Configuration (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:formom}?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:formom1234}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### MySQL 권한 설정
```bash
# Docker MySQL 시작
docker-compose up -d

# 권한 부여 (필요시)
docker exec formom-mysql mysql -uroot -pformom1234 -e \
  "CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'formom1234'; \
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; \
   FLUSH PRIVILEGES;"
```

---

## 테스트 결과

### API 테스트
```
GET http://localhost:8080/api/news/search?keyword=임영웅&count=5
```

**응답 예시** (성공):
```json
[
  {
    "id": null,
    "title": "[단독] 이찬원, 트롯 왕좌·2년 연속 연예대상 노린다…",
    "content": "먼저 '미스터트롯'에서 임영웅, 영탁에 이어...",
    "summary": null,
    "source": "네이버뉴스",
    "url": "https://m.entertain.naver.com/article/109/0005432502",
    "thumbnailUrl": null,
    "publishedAt": "2025-11-17",
    "importanceScore": null
  }
]
```

---

## 참고 자료

- [MySQL 8.0 인증 방식 변경](https://dev.mysql.com/doc/refman/8.0/en/upgrading-from-previous-series.html#upgrade-caching-sha2-password)
- [Spring Boot 환경 변수 설정](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Docker MySQL 권한 설정](https://hub.docker.com/_/mysql)

---

## 체크리스트 (다음 설정 시 참고)

- [ ] Gradle wrapper 생성 확인
- [ ] Docker Compose로 MySQL 실행
- [ ] MySQL 권한 설정 (`root@'%'`)
- [ ] `application.yml`에 `allowPublicKeyRetrieval=true` 추가
- [ ] 환경 변수 기본값을 개발 환경에 맞게 설정
- [ ] `.env` 파일이 `.gitignore`에 포함되어 있는지 확인
- [ ] API 키가 코드에 하드코딩되지 않았는지 확인
