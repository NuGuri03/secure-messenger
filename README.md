# Secure Messenger Application

Secure Messenger는 Java로 개발된 암호화 메신저 애플리케이션입니다

### 너와 나만 볼 수 있게
- 사용자가 주고받는 메시지는 클라이언트에서 암호화되어 서버조차 내용을 볼 수 없습니다. 

### 절대 유출되지 않도록 설계했습니다
- Argon2id 기반 비밀번호 해싱으로 안전한 사용자 인증을 구현했습니다. 
- 전자서명, 세션 키 생성, 암호화된 TCP 통신 등을 통해 높은 보안성을 확보했습니다.
- 서버 데이터베이스가 유출되더라도, 개인 메세지의 노출은 불가능합니다.

---

## ✨ Features

### 💬 채팅 기능
- 1:1 채팅 (그룹 채팅은 추후 확장 예정)
- 텍스트 메시지 송수신
- 채팅 말풍선 UI (닉네임, 아바타, 정렬 포함)

### 👤 사용자 인증 및 계정 관리
- 회원가입
- 아이디 중복 확인
- 비밀번호 재입력 확인
- 비밀번호 유효성 검사 (길이, 특수문자 포함 등)
- 로그인/로그아웃
- 암호화된 세션 키 생성 및 유지

### 🛡 보안 기능
- Argon2id 비밀번호 해싱
  - 사용자 비밀번호는 Argon2id 알고리즘으로 해싱되어 저장
- 세션 키 암호화 통신
  - 클라이언트는 서버의 공개키로 세션 키를 암호화해 전송, 이후 메시지는 해당 키로 대칭 암호화
- RSA 기반 End-to-End 암호화
  - 클라이언트는 서버의 공개키로 세션 키를 암호화, 서버는 메시지를 복호화하지 않고 수신자의 공개키로 다시 암호화해 전달

## ⚙️ Build & Run
```bash
gradlew build           
gradlew.bat runClient   
gradlew.bat runServer
```

---

## 🛠️ Tech Stack
- **Language**: Java (JDK 21)
- **WebSocket**: Java TCP Socket
- **Crypto**: `java.security` package
- **Database**: SQLite
- **Build Tool**: Gradle

---

## 📦 Project Structure
```
secure-messenger/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── client/               # 클라이언트 애플리케이션 (UI 포함)
│   │   │   ├── server/               # 서버 애플리케이션
│   │   │   ├── networked/            # 공통 클래스
│   └── main/
│       └── resources/                # 리소스 파일
├── build.gradle                      
├── settings.gradle                   
├── README.md
└── .gitignore
```

---

