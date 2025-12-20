# API Testing Guide

## Prerequisites

1. **Start PostgreSQL**:
   ```bash
   docker run --name cbt-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=cbt_platform -p 5432:5432 -d postgres:16
   ```

2. **Start Application**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Access Swagger UI**: http://localhost:8080/swagger-ui.html

---

## Default Admin Credentials

- **Email**: `admin@cbt.com`
- **Password**: `Admin123!`

---

## Test Scenarios

### 1. Login as Admin

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@cbt.com",
  "password": "Admin123!"
}
```

**Expected Response**:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "user": {
    "id": "uuid...",
    "email": "admin@cbt.com",
    "name": "System Administrator",
    "role": "ADMIN",
    ...
  }
}
```

**Save the `accessToken` for subsequent requests!**

---

### 2. Load Anxiety Course from Scenario

```bash
POST http://localhost:8080/api/courses/load-scenario
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json

{
  "scenarioPath": "scenarios/anxiety/scenario_1.0.1.json"
}
```

**Expected Response**: 201 Created
```json
{
  "id": "uuid...",
  "slug": "anxiety",
  "name": "CBT Anxiety Course - Free Part",
  "description": "Orchestrator scenarios for LLM-powered CBT course focused on anxiety and panic",
  "scenarioJson": {
    "meta": { ... },
    "global_config": { ... },
    "sessions": { ... }
  },
  "version": "1.0.1",
  "freeSessions": 2,
  "category": "anxiety",
  "isActive": true,
  "isPublished": false,
  ...
}
```

---

### 3. Publish the Course

```bash
PUT http://localhost:8080/api/courses/{course-id}
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json

{
  "isPublished": true
}
```

---

### 4. Get Course as User (Public Endpoint)

```bash
GET http://localhost:8080/api/courses/anxiety
```

**Expected Response**: 200 OK (full course with scenario JSON)

---

### 5. List All Published Courses

```bash
GET http://localhost:8080/api/courses
```

**Expected Response**: Array of courses

---

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@cbt.com","password":"Admin123!"}'
```

### Load Scenario
```bash
TOKEN="your-token-here"

curl -X POST http://localhost:8080/api/courses/load-scenario \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"scenarioPath":"scenarios/anxiety/scenario_1.0.1.json"}'
```

### Get Course
```bash
curl http://localhost:8080/api/courses/anxiety
```

---

## Troubleshooting

### Database Connection Error
- Ensure PostgreSQL is running: `docker ps`
- Check connection: `psql -h localhost -U postgres -d cbt_platform`

### Flyway Migration Error
- Check logs: `mvn spring-boot:run`
- Verify migrations: `ls src/main/resources/db/migration/`

### Scenario File Not Found
- Verify file exists: `ls backend/resources/scenarios/anxiety/scenario_1.0.1.json`
- Path should be relative to classpath

### 401 Unauthorized
- Token expired (24 hours)
- Login again to get new token

---

## Next Steps

1. ✅ Test user registration
2. ✅ Test course loading
3. ⏭️ Implement UserProgress module
4. ⏭️ Implement CourseEngine
