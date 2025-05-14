# Secure Messenger Service

2025-1 자바프로그래밍 팀 프로젝트  
Java를 이용한 암호화 메신저 서비스입니다.

---

## ✨ Features

- 1:1 채팅(그룹 채팅)
  - 텍스트 메세지
    - 메세지 입력창 UI, UX
    - 보낸 시간 표시
    - 채팅 내역은 DB에 **암호화**하여 저장
  - 읽지 않은 유저 수 카운트
- 회원가입
  - 아이디 중복 확인
  - 비밀번호 재입력 확인
  - 비밀번호 유효성(비밀번호 길이 및 특수문자 포함 여부) 확인
  - 비밀번호는 DB에 **암호화**하여 저장
- 로그인/로그아웃
  - 자동 로그인
  - 비밀번호 찾기

## ⚙️ Build & Run
```bash
gradlew build           # Build the project
gradlew.bat runClient   # Run the client application
gradlew.bat runServer   # Run the server application
```

---

## 🛠️ Tech Stack
- **Language**: Java (JDK 21)
- **WebSocket**: Java TCP Socket
- **Crypto**: `java.security` package
- **Database**: MySQL
- **Build Tool**: Gradle

---

## 📦 Project Structure
```
secure-messenger/
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── client/               # 클라이언트 애플리케이션 (UI 포함)
│   │   │   ├── server/               # 서버 애플리케이션
│   │   │   ├── crypto/               # 암호화 로직
│   │   │   ├── common/               # 공통 클래스
│   └── main/
│       └── resources/                # 리소스
├── build.gradle                      
├── settings.gradle                   
└── .gitignore
```

---

