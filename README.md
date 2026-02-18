# JavaFX Chat App 💬

A real-time chat client built with **JavaFX 25** and powered by [ntfy](https://docs.ntfy.sh/) as the messaging backend.

![Java](https://img.shields.io/badge/Java-25-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25-blue)
![Build](https://img.shields.io/badge/build-Maven-green)

---
## 👥 About This Project

This project was developed as a school assignment during my studies.
I'm including it in my portfolio as it demonstrates real-time messaging, JavaFX, MVC architecture, and testing.

---

## ✨ Features

- **MVC architecture** — clean separation between `HelloFX` (app), `HelloController` (controller), and `HelloModel` (model)
- **Send messages** to a configurable ntfy topic via [JSON POST](https://docs.ntfy.sh/publish/#publish-as-json)
- **Receive messages** in real time via [JSON stream](https://docs.ntfy.sh/subscribe/api/)
- **Environment-based configuration** — backend URL loaded from a `.env` file (excluded from version control)
- **File attachments** — send files via an "Attach local file" option
- **Unit tested** — model layer covered with JUnit 5, AssertJ, and Mockito
- **CI/CD** — GitHub Actions workflow for compilation checks, test execution, and autograding

## 🛠️ Tech Stack

| Layer         | Technology                                                    |
|---------------|---------------------------------------------------------------|
| Language      | Java 25                                                       |
| UI Framework  | JavaFX 25 (Controls + FXML)                                   |
| HTTP          | `java.net.http` (built-in Java HTTP Client)                   |
| JSON          | Jackson Databind 3.0                                          |
| Configuration | [dotenv-java](https://github.com/cdimascio/dotenv-java) 3.2  |
| Testing       | JUnit Jupiter 6, AssertJ 3.27, Mockito 5.20, WireMock 4.0    |
| Build         | Maven (with Maven Wrapper)                                    |

## 📋 Prerequisites

- **JDK 25** (set `JAVA_HOME` accordingly)
- No separate Maven installation required — the included Maven Wrapper (`mvnw`) handles it

## 🚀 Getting Started

### 1. Configure environment

Create a `.env` file in the project root with your ntfy backend URL:
```
NTFY_URL=https://ntfy.sh
```

> The `.env` file is listed in `.gitignore` and will **not** be committed.

### 2. Build & Run
```bash
./mvnw clean javafx:run
```

On Windows:
```cmd
mvnw.cmd clean javafx:run
```

## 📁 Project Structure
```
JavaFX-Chat-App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── com/example/
│   │   │       ├── HelloFX.java             # Application entry point
│   │   │       ├── HelloController.java     # FXML controller
│   │   │       ├── HelloModel.java          # Business logic / model
│   │   │       ├── NtfyConnection.java      # Connection abstraction
│   │   │       ├── NtfyConnectionImpl.java  # HTTP-based ntfy connection
│   │   │       └── NtfyMessageDto.java      # Message data transfer object
│   │   └── resources/com/example/
│   │       └── hello-view.fxml              # FXML UI layout
│   └── test/java/com/example/
│       ├── HelloModelTest.java              # Model unit tests
│       ├── NtfyConnectionSpy.java           # Test spy
│       └── NtfyConnectionStub.java          # Test stub
├── .github/workflows/classroom.yml          # CI autograding workflow
├── pom.xml
└── README.md
```

---

## 📜 License
This project is for educational purposes.