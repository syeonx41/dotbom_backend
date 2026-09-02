# 돋봄 (Dotbom) — Backend

난독증 등 읽기에 어려움을 겪는 사용자를 위한 맞춤형 읽기 지원 서비스의 백엔드 저장소입니다.
글자 크기·글꼴·색상 등을 개인 맞춤으로 조절하는 가독성 뷰어, AI 기반 문서 요약·쉬운 문장 변환, 읽기 능력 훈련 게임 기능을 제공합니다.

## 데모

비용 절감을 위해 배포 서버를 상시 구동하지 않고 있습니다. 아래 영상에서 실제 동작을 확인할 수 있습니다.

[![데모 영상](https://img.youtube.com/vi/C1Ak7-vaBVU/0.jpg)](https://www.youtube.com/watch?v=C1Ak7-vaBVU)


---

## 프로젝트 배경

교내 대학일자리플러스센터·GDGOC 주최 1박 2일 해커톤 **"강냉톤"**에서 팀 **알밤이네**로 참가해 만든 프로젝트이며, 해커톤에서 최우수상 수상했습니다.
다만 해커톤 기간 내 정식 배포까지는 마무리하지 못했고 이후 개인적으로 코드를 다시 살펴보며 **1년 뒤 혼자 리팩토링 및 재배포**를 진행했습니다. 
이 저장소는 그 리팩토링 과정이 담긴 개인 브랜치(`refactor/personal`)를 기준으로 합니다.

## 주요 기능

- **사용자 맞춤 설정**: 읽기 선호도 설문 기반으로 글자 크기·글꼴·색상·배경·어휘 수준 개인화
- **가독성 향상 뷰어**: 글자 크기, 자간, 글꼴, 배경색 직접 조정
- **AI 문서 요약**: Naver Clova OCR로 이미지/PDF에서 텍스트 추출 후, OpenAI GPT로 핵심 내용 요약
- **쉬운 문장 변환**: 어려운 표현을 쉬운 단어로 변환
- **읽기 훈련 게임**: 받침 고르기, 그림에 맞는 문장 고르기 등 읽기·이해 능력 훈련, 서버 기준 훈련 기회(횟수) 관리

## 기술 스택

| 구분 | 내용 |
|---|---|
| Language / Framework | Java 17, Spring Boot 3.5.4, Spring Data JPA |
| Database | MySQL (해커톤 당시 MariaDB → 개인 리팩토링 중 전환) |
| 외부 연동 | Naver Clova OCR, OpenAI GPT API (WebFlux `WebClient`) |
| 문서 처리 | Apache PDFBox (PDF → 이미지 변환 후 OCR) |
| 인증 | UUID 쿠키 기반 사용자 식별 (`ResponseCookie`, `SameSite`/`Secure` 환경별 분리) |
| 배포 | AWS EC2, nginx(reverse proxy), Let's Encrypt(HTTPS), systemd |

## 배포 구조

```
[사용자 브라우저]
      │  HTTPS
      ▼
[Vercel] dotbom-frontend.vercel.app  (React + Vite, refactor/personal 브랜치)
      │  HTTPS (fetch, credentials 포함)
      ▼
[EC2] nginx (443, Let's Encrypt) ── reverse proxy ──▶ Spring Boot (8080, systemd)
                                                              │
                                                              ▼
                                                        MySQL (localhost)
```

도메인 없이 [nip.io](https://nip.io)로 EC2 퍼블릭 IP에 HTTPS 인증서를 발급받아 별도 도메인 구매 없이 프론트(HTTPS)–백엔드(HTTPS) 간 쿠키 인증이 정상 동작하도록 구성했습니다.

## 주요 API 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/main` | 최초 진입 시 UUID 쿠키 발급 |
| POST | `/api/v1/viewer/file-upload` | 이미지/PDF 업로드 |
| DELETE | `/api/v1/viewer/file-delete/{fileId}` | 업로드 파일 삭제 |
| POST | `/api/v1/viewer/ai` | OCR + GPT 기반 요약/변환 |
| GET | `/api/v1/training/load` | 훈련 문제 목록 조회 |
| POST | `/api/v1/training/start` | 훈련 시작 (서버에서 남은 기회 검증 후 차감) |
| GET | `/api/v1/training/attempts` | 현재 남은 훈련 기회 조회 |

## 개인 리팩토링 하이라이트

해커톤 이후 실제로 동작하는 서비스 관점에서 코드를 다시 점검하며 발견하고 고친 문제들입니다.

- **CORS/쿠키 인증이 한 번도 실행된 적이 없었음**: 쿠키를 발급하는 유일한 엔드포인트(`/api/v1/main`)를 프론트가 호출한 적이 없어서 인증 로직 자체가 처음부터 죽어있던 상태였음을 발견하고 연결
- **DB 스키마 설계 누락**: `User` 엔티티는 있었지만 `schema.sql`에 `user` 테이블 생성 구문이 빠져 있던 버그 발견 및 수정
- **Path Traversal 취약점**: 파일 업로드/삭제 API가 클라이언트 입력값을 검증 없이 파일 경로에 사용하던 문제를 `normalize()` + `startsWith()` 검증으로 방어
- **훈련 기회(문제풀이 횟수)가 프론트 `localStorage`로만 관리되어 쉽게 우회 가능했던 구조**를 서버 검증(DB 기준 차감)으로 전환
- **도메인 없이 무료 HTTPS 배포**: `nip.io` + Let's Encrypt를 활용해 비용 없이 프론트–백엔드 간 크로스사이트 쿠키 인증이 동작하는 환경 구성

## 로컬 실행

```bash
git clone -b refactor/personal https://github.com/syeonx41/dotbom_backend.git
cd dotbom_backend
```

`src/main/resources/application.properties`를 아래 형식으로 생성 (git에는 포함되지 않음 — 민감정보 포함):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dotbom?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always

clova.ocr.secret-key=
clova.ocr.base-url=
clova.ocr.path=

openai.api.key=

app.cors.allowed-origin=http://localhost:5173
app.cookie.secure=false
app.cookie.same-site=Lax
```

```bash
./gradlew build
java -jar build/libs/teamalbam-0.0.1-SNAPSHOT.jar
```

## 관련 저장소
- Frontend: https://github.com/syeonx41/dotbom_frontend
