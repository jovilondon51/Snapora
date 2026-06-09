# Snapora 📸

A full-stack Instagram clone built with Java Spring Boot, MySQL, and Vanilla JavaScript.

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=java) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring-boot) ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6-F7DF1E?logo=javascript) ![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ Features

- 🔐 JWT authentication with refresh token rotation
- 📸 Post photos with captions and location
- ❤️ Like & unlike posts (double-tap supported)
- 💬 Nested comments & replies
- 👥 Follow / Unfollow users
- 📖 Stories with 24h expiry & progress bar viewer
- 🔔 Real-time notifications via SSE
- 🔖 Save posts
- 🎬 Reels (vertical scroll video/image feed)
- 🔍 User search with debounce
- 🌙 Dark mode (persisted in localStorage)
- 📱 Mobile-first responsive design

---

## 🗃️ Database ER Diagram

```
users ──< posts ──< post_likes
  |          └──< comments (self-ref)
  |          └──< saved_posts
  ├──< follows (follower → following)
  ├──< stories
  └──< notifications (recipient ← actor)
```

---

## 🚀 Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0
- A static file server for frontend (e.g. VS Code Live Server)

### 1. Database
```bash
mysql -u root -p < schema.sql
mysql -u root -p snapora < sample-data.sql
```

### 2. Backend
```bash
# Set env var (or edit application.properties)
export JWT_SECRET=your-very-secret-key-min-32-chars

# Edit src/main/resources/application.properties with your DB password
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`

### 3. Frontend
Open `frontend/` with VS Code Live Server (port 5500) or any static server:
```bash
cd frontend && npx serve -p 5500
```
Open `http://localhost:5500/index.html`

---

## 🔑 Environment Variables

| Variable | Default | Description |
|---|---|---|
| `JWT_SECRET` | `snapora-super-secret-jwt-key-min-32-characters-long` | JWT signing key (min 32 chars) |
| `spring.datasource.password` | `your_password` | MySQL password |
| `app.upload.dir` | `uploads` | File upload directory |

---

## 📡 API Endpoints

| Method | Endpoint | Auth |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| POST | `/api/auth/refresh` | Public |
| GET | `/api/users/me` | ✅ |
| GET | `/api/users/{username}` | ✅ |
| PUT | `/api/users/me` | ✅ |
| POST | `/api/users/me/avatar` | ✅ |
| GET | `/api/users/search?q=` | ✅ |
| POST | `/api/posts` | ✅ |
| GET | `/api/posts/feed` | ✅ |
| GET | `/api/posts/explore` | ✅ |
| GET | `/api/posts/{id}` | ✅ |
| DELETE | `/api/posts/{id}` | ✅ |
| POST | `/api/posts/{id}/like` | ✅ |
| POST | `/api/posts/{id}/save` | ✅ |
| POST | `/api/posts/{id}/comments` | ✅ |
| GET | `/api/posts/{id}/comments` | ✅ |
| POST | `/api/users/{id}/follow` | ✅ |
| GET | `/api/users/{id}/followers` | ✅ |
| GET | `/api/users/{id}/following` | ✅ |
| POST | `/api/stories` | ✅ |
| GET | `/api/stories/feed` | ✅ |
| GET | `/api/stories/{id}` | ✅ |
| GET | `/api/notifications` | ✅ |
| PUT | `/api/notifications/{id}/read` | ✅ |
| GET | `/api/notifications/stream` | ✅ SSE |

---

## 📁 Project Structure

```
Snapora/
├── src/main/java/com/snapora/     # Spring Boot backend
│   ├── controller/                # REST controllers
│   ├── service/                   # Business logic
│   ├── repository/                # JPA repositories
│   ├── model/                     # Entities, DTOs, enums
│   ├── security/                  # JWT filter & provider
│   ├── config/                    # Security, web, SSE config
│   ├── exception/                 # Global error handling
│   └── util/                      # File storage
├── frontend/                      # Vanilla JS frontend
│   ├── index.html                 # Login / Register
│   ├── feed.html                  # Main feed
│   ├── explore.html               # Explore & search
│   ├── profile.html               # User profile
│   ├── notifications.html         # Notifications
│   ├── reels.html                 # Reels page
│   ├── css/                       # Stylesheets
│   └── js/                        # JavaScript modules
├── schema.sql                     # Database schema
├── sample-data.sql                # Demo data
└── pom.xml                        # Maven build
```

---

## 📸 Screenshots

> Add screenshots here after running the app.

---

## 📜 License

MIT © 2024 Snapora
