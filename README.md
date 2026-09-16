# Online Quiz Management & Assessment System

Spring Boot + MySQL + JWT backend, plus a plain HTML/CSS/JS frontend
served as static resources from the same app. Covers Phases 1–17 of the
project spec: management CRUD, auth, the quiz engine (random selection,
server-authoritative timer, scoring), leaderboard, admin statistics, and
the full set of user/admin pages.

## Frontend pages

Served from `src/main/resources/static/` — no separate server or build
step needed, `mvn spring-boot:run` serves everything from `:8080`.

- `index.html` / `register.html` — sign in / create account
- `dashboard.html` — browse published quizzes
- `quiz-attempt.html` — the timed attempt screen (server-authoritative
  countdown, question navigation, immediate answer save per Section 18)
- `result.html`, `review.html` — score summary, then answer review on request
- `history.html`, `profile.html`, `leaderboard.html`
- `admin/dashboard.html`, `admin/categories.html`, `admin/quizzes.html`,
  `admin/questions.html`, `admin/users.html`

The JWT is kept in `localStorage` and attached by `js/api.js` on every
call; `requireAuth('ADMIN' | 'USER')` on each page redirects to login if
there's no token, or to the correct dashboard if the role doesn't match
what the page needs — though the real enforcement is still server-side
(Spring Security), this is just so a USER doesn't land on a broken admin
screen. There's no build tooling here on purpose — it's plain JS so it's
easy to read line-for-line in an interview.

## Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8.x running locally (or reachable) — the app auto-creates the
  database on first run (`createDatabaseIfNotExist=true`), but the MySQL
  *server* itself must already be running.

## Configuration (environment variables)

All of these have local-dev defaults baked into `application.yml`, so the
app runs with **zero configuration** against a local MySQL with the
default `root`/`root` credentials. Override any of them as needed:

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `quiz_system` | Database name (auto-created) |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (a long placeholder) | HMAC key for signing JWTs — **change this** for anything beyond local testing |
| `JWT_EXPIRATION_MS` | `3600000` (1 hour) | Token lifetime |
| `SECONDS_PER_QUESTION` | `60` | Section 13's TIME_PER_QUESTION constant |
| `ADMIN_EMAIL` | `admin@quizapp.com` | Seeded admin login |
| `ADMIN_PASSWORD` | `Admin@123` | Seeded admin login |

## Running it

```bash
cd quiz-system
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. On first startup, watch the
console for:

```
Seeded default admin account -> email: admin@quizapp.com
```

That confirms `AdminSeeder` created your one way in as an administrator
(public registration only ever creates USER accounts, by design —
Section 4/5).

## Testing the flow with Postman (or curl)

1. **Login as admin**
   `POST /api/auth/login` — `{"email": "admin@quizapp.com", "password": "Admin@123"}`
   → copy the `token` from the response.

2. **Create a category** (send `Authorization: Bearer <admin token>` on every admin call from here on)
   `POST /api/admin/categories` — `{"name": "Java", "description": "Core Java"}`

3. **Create a quiz**
   `POST /api/admin/quizzes` — `{"title": "Java Basics", "categoryId": 1, "questionsPerAttempt": 2}`

4. **Add at least 2 questions** to it (repeat with different text/options)
   `POST /api/admin/quizzes/1/questions`
   ```json
   {
     "text": "What keyword defines a class in Java?",
     "options": [
       {"text": "class", "correct": true},
       {"text": "struct", "correct": false},
       {"text": "define", "correct": false}
     ]
   }
   ```

5. **Publish it**
   `POST /api/admin/quizzes/1/publish` — fails with a 400 if you have fewer questions than `questionsPerAttempt` (Section 8).

6. **Register a normal user**
   `POST /api/auth/register` — `{"name": "Alice", "email": "alice@test.com", "password": "test123"}`
   → copy that user's `token` for the rest of the flow.

7. **Browse quizzes** (as the user)
   `GET /api/quizzes`

8. **Start an attempt**
   `POST /api/quizzes/1/attempts` → returns questions **without** any `correct` field, plus `expiryTime`.

9. **Answer a question**
   `POST /api/attempts/{attemptId}/answers` — `{"questionId": 1, "selectedOptionId": 3}`

10. **Submit**
    `POST /api/attempts/{attemptId}/submit` → returns the scored result.

11. **Review answers**
    `GET /api/attempts/{attemptId}/review` — correct answers only appear here, after submission.

12. **Check history / progress / leaderboard**
    `GET /api/attempts/history`, `GET /api/attempts/progress`, `GET /api/quizzes/1/leaderboard`

13. **Admin dashboard**
    `GET /api/admin/stats`, `GET /api/admin/users`

## What's intentionally not here yet

- **Frontend** (HTML/CSS/JS) — Phase 17 in the project's own dev order.
- **Refresh-token rotation** — a single access token with a 1-hour expiry
  is used, matching the spec's scope; a refresh-token flow would be a
  reasonable "future work" talking point in an interview but wasn't asked for.
