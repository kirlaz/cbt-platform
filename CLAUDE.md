# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# CBT Platform
> Config-driven платформа для CBT-курсов (стресс, самооценка, прокрастинация)

Реализуем сервис виртуального психолога, который работает по КПТ подходу на основе LLM. 
Сервис реализует различные курсы по пситотерапии на основе КПТ.
Для MVP реализуем курс «Управление тревогой и стрессом на работе и в повседневной жизни». 
Далее планируем курсы под другие распространенные проблемы: Депрессия, Самооценка, Выгорание, отношения, прокрастинация и т.п.

Каждый курс состоит из нескольких КПТ сессий. На каждой сессии пользователю должны выдаваться задания и домашние задания между сессиями. 
Курс должен быть персонализированнмы: корректироваться и подстраиваться в зависимости от ответов пользователя. 
Часть сессия должны быть бесплатной, далее за плату. 
Важно чтобы пользователь почувствовал персонификацию и результат(Quick wins) уже в бесплатной части курса, чтобы оплатил подписку. 

Курс должен управляться сценарием описанным в json.


**Project Status**: 🚧 In Development - Core modules being implemented

## Quick Start

```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Scenario Editor (Web)
cd scenario-editor-front && npm install && npm run dev

# Mobile App
cd mobile && npx expo start
```

## Project Structure

```
cbt-platform/
├── backend/                 # Java Spring Boot API
│   ├── CONVENTIONS.md       # Конвенции backend
│   └── src/main/resources/scenarios	# Версии сценариев курса
│       └── anxiety         # Сценарии для курса Управление тревогой и стрессом на работе и в повседневной жизни
│   └── src/main/java/com/cbt/platform/
│       ├── config/          # Конфигурации
│       ├── security/        # JWT, auth
│       ├── common/          # Общие классы, exceptions
│       ├── user/            # Пользователи
│       ├── course/          # Курсы
│       ├── session/         # Сессии
│       ├── progress/        # Прогресс
│       ├── checkin/         # Check-ins
│       ├── technique/       # Техники
│       ├── subscription/    # Подписки
│       ├── notification/    # Уведомления
│       ├── gamification/    # Streaks, achievements
│       ├── engine/          # CourseEngine (ядро)
│       ├── llm/             # Claude API интеграция
│       └── editor/          # Scenario Editor backend (API для визуального редактора)
├── scenario-editor-front/   # React Web App (визуальный редактор сценариев)
│   ├── GETTING_STARTED.md   # Инструкция по запуску
│   └── src/
│       ├── components/      # UI компоненты
│       │   └── layout/      # Layout (Header, Sidebar)
│       ├── pages/           # Страницы (Login, Drafts, Editor, Templates)
│       ├── services/        # API интеграция
│       ├── store/           # Zustand stores
│       ├── types/           # TypeScript типы
│       └── App.tsx          # Root component with routing
├── mobile/                  # React Native + Expo (мобильное приложение для пользователей)
│   ├── CONVENTIONS.md       # Конвенции frontend
│   └── src/
│       ├── screens/         # Экраны
│       ├── components/      # Компоненты
│       │   ├── blocks/      # Рендереры блоков
│       │   ├── exercises/   # Упражнения
│       │   └── visualizations/
│       ├── store/           # Zustand stores
│       ├── services/        # API
│       ├── hooks/           # Custom hooks
│       ├── types/           # TypeScript типы
│       └── constants/       # Theme, config
└── docs/                    # Документация
    ├── architecture.md
    ├── api-spec.md
    └── course-schema.md
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 3.2, PostgreSQL 16, Redis 7 |
| Editor Frontend | React 18, Vite 5, TypeScript 5, Material-UI 5, Zustand 4 |
| Mobile Frontend | React Native 0.73, Expo 50, TypeScript 5, Zustand 4 |
| LLM | Claude API (claude-3-5-sonnet) |
| Auth | JWT |
| Payments | RevenueCat |

## Core Concepts

### CourseEngine (Backend)
**Ключевая архитектурная концепция**: Интерпретатор JSON-сценариев курсов.

- **Курс** = JSON файл с массивом сессий, каждая сессия содержит блоки
- **CourseEngine** обрабатывает блоки последовательно, управляя переходами через `next` и `conditionalNext`
- **BlockHandlerRegistry** регистрирует обработчики для каждого типа блока (Strategy pattern)
- Каждый handler имеет метод `handle(Block block, UserData userData)` возвращающий `BlockResult`
- Engine сохраняет состояние в `UserProgress.currentSessionId`, `currentBlockIndex`, `userData`

### Block Types
12 типов блоков с разными интерактивными возможностями:
- **Content**: `STATIC` (просто текст)
- **Input**: `INPUT`, `SLIDER`, `SINGLE_SELECT`, `MULTI_SELECT` (собирают данные)
- **LLM**: `LLM_CONVERSATION` (чат), `LLM_RESPONSE` (генерация на основе userData)
- **Special**: `EXERCISE`, `VISUALIZATION`, `CALCULATION`, `SESSION_COMPLETE`, `PAYWALL`

### UserData (JSONB)
Центральная структура данных пользователя по курсу:
- Накапливает все введенные данные: `name`, `stressLevel`, `triggers`, `thoughtRecords[]`
- Используется в шаблонах: `"Привет, {{name}}!"` → `"Привет, Иван!"`
- Передается в Claude API для персонализированных ответов
- Хранится как JSONB в PostgreSQL для гибкой схемы

**Template Resolution**:
- `{{key}}` - простое поле
- `{{triggers[0]}}` - элемент массива
- `{{user.profile.name}}` - вложенные поля

### Scenario Editor (Backend)
**Визуальный редактор сценариев для психологов/контент-мейкеров** (не-технических пользователей).

- **ScenarioDraft** - черновики сценариев перед публикацией (хранятся отдельно от Course)
- **Workflow**: Draft → Edit → Validate → Publish → Course
- **Validation Service** - проверка структуры, ссылок между блоками, circular dependencies
- **Block Templates** - библиотека готовых блоков для быстрого старта
- **Version History** - отслеживание изменений черновиков
- **Role-based Access**: ADMIN, EDITOR (создание/редактирование), VIEWER (только просмотр)

**API Endpoints**:
```
GET/POST/PUT/DELETE  /api/editor/scenarios        # CRUD черновиков
GET/POST/PUT/DELETE  /api/editor/scenarios/{id}/drafts   # История версий
POST                 /api/editor/scenarios/{id}/publish   # Публикация → Course
POST                 /api/editor/validate                 # Валидация
GET                  /api/editor/templates/blocks         # Шаблоны блоков
```

## Key Files & Patterns

### Backend Architecture
| File Path Pattern | Purpose | Key Pattern |
|-------------------|---------|-------------|
| `backend/src/.../engine/CourseEngine.java` | Ядро обработки сценариев | Orchestrates block processing, manages state transitions |
| `backend/src/.../engine/BlockHandlerRegistry.java` | Регистрирует все handlers | Strategy pattern - maps BlockType → Handler |
| `backend/src/.../engine/handlers/*Handler.java` | Handler на каждый BlockType | Implements `BlockHandler` interface |
| `backend/src/.../llm/ClaudeService.java` | Claude API интеграция | Handles prompts, streaming, context building |
| `backend/src/.../{module}/entity/*.java` | JPA entities | UUID ids, JSONB fields, audit timestamps |
| `backend/src/.../common/exception/BaseException.java` | Base для всех бизнес-exceptions | Carries HTTP status + error code |

### Scenario Editor Frontend Architecture
| File Path Pattern | Purpose | Key Pattern |
|-------------------|---------|-------------|
| `scenario-editor-front/src/services/api.ts` | Axios client with JWT interceptors | Auto-adds token, handles 401 refresh |
| `scenario-editor-front/src/services/*Service.ts` | API integration for each domain | draftService, templateService, authService |
| `scenario-editor-front/src/store/*Store.ts` | Zustand state management | authStore, draftStore, templateStore |
| `scenario-editor-front/src/pages/*Page.tsx` | Page components (routes) | LoginPage, DraftsPage, EditorPage, TemplatesPage |
| `scenario-editor-front/src/components/layout/Layout.tsx` | Main layout with sidebar | AppBar + Drawer + Outlet |
| `scenario-editor-front/src/types/*.ts` | TypeScript type definitions | auth, draft, block, template, api types |

### Mobile App Frontend Architecture
| File Path Pattern | Purpose | Key Pattern |
|-------------------|---------|-------------|
| `mobile/src/components/blocks/BlockRenderer.tsx` | Диспетчер рендеринга | Maps BlockType → Component (lookup object) |
| `mobile/src/components/blocks/*Block.tsx` | Компонент на каждый BlockType | Receives `block`, `userData`, `onComplete` props |
| `mobile/src/store/useSessionStore.ts` | Состояние текущей сессии | Zustand store, manages navigation through blocks |
| `mobile/src/store/useUserDataStore.ts` | Накопленные userData | Persisted to AsyncStorage |
| `mobile/src/utils/template.ts` | Резолвинг {{templates}} | Replaces placeholders with userData values |
| `mobile/src/services/api.ts` | Axios instance с interceptors | Auto-adds JWT, handles 401 refresh |

### Critical Conventions
- **Backend**: ALL module creation follows package structure in `CONVENTIONS.md`
- **Mobile Frontend**: ALL components use TypeScript interfaces (not types) for props (see `CONVENTIONS.md`)
- **Editor Frontend**: React + TypeScript + Material-UI standard patterns

## Testing Strategy

### Test Pyramid
```
         /\      E2E (5%) - Full user flows через REST API
        /  \
       /____\    Integration (25%) - Database + Service + Security
      /      \
     /        \  Unit (70%) - Handlers, Services, Utils
    /__________\
```

### Backend Testing

**Stack**: JUnit 5, Mockito, Spring Boot Test, Testcontainers (PostgreSQL), H2 (для быстрых unit тестов)

**Structure**:
```
backend/src/test/java/com/cbt/platform/
├── unit/                    # Изолированные тесты (@ExtendWith(MockitoExtension))
│   ├── engine/              # CourseEngine, BlockHandlers
│   ├── course/              # CourseService логика
│   ├── progress/            # ProgressService логика
│   └── security/            # JWT, auth логика
├── integration/             # @SpringBootTest + @Testcontainers
│   ├── course/              # CourseController + Repository + DB
│   ├── progress/            # ProgressController + Repository + DB
│   └── user/                # AuthController + Security
├── e2e/                     # Full flows (register → start course → complete session)
└── fixtures/                # TestDataBuilder, test scenarios
```

**Key Patterns**:
- **AAA**: Arrange → Act → Assert
- **Naming**: `shouldDoSomethingWhenCondition()`
- **Isolation**: Mock external dependencies (LLM API), use Testcontainers for DB
- **Test Data**: Centralized `TestDataBuilder` для fixture creation
- **Transactions**: `@Transactional` на integration tests для auto-rollback

**Critical Test Areas**:
1. **CourseEngine** - block processing, state transitions, conditional navigation
2. **BlockHandlers** - каждый из 12 типов блоков (STATIC, INPUT, LLM_CONVERSATION, etc.)
3. **UserData JSONB** - merge/replace, template resolution
4. **Auth Flow** - register, login, JWT refresh
5. **Progress Tracking** - session/block completion, course completion

### Frontend Testing

**Stack**: Jest, React Testing Library, jest-expo

**Structure**:
```
mobile/src/
├── __tests__/               # Unit tests рядом с кодом
│   ├── utils/               # template.test.ts, validation.test.ts
│   ├── store/               # useSessionStore.test.ts, useUserDataStore.test.ts
│   └── services/            # api.test.ts
└── components/
    └── blocks/
        └── __tests__/       # StaticBlock.test.tsx, InputBlock.test.tsx
```

**Key Patterns**:
- **Test Behavior**: Not implementation details (avoid testing internal state)
- **User-Centric**: Use `getByRole`, `getByLabelText` (accessibility-friendly)
- **Mock API**: Mock axios calls, not Zustand stores
- **Avoid Snapshots**: They break on styling changes, prefer explicit assertions

**Critical Test Areas**:
1. **BlockRenderer** - правильный выбор компонента по BlockType
2. **Template Resolution** - `{{name}}`, `{{triggers[0]}}`
3. **Session Store** - навигация по блокам, сохранение userData
4. **Input Blocks** - validation, onComplete callbacks

### Coverage Goals
- **Backend**: 80% unit, 60% integration
- **Frontend**: 70% components, 80% utils/stores
- **Critical Paths**: 100% (auth, course engine, payments)

### Running Tests

**Backend**:
```bash
./mvnw test                      # All tests
./mvnw test -Dtest=CourseEngine* # Specific class
./mvnw verify                    # Tests + code quality checks
```

**Frontend**:
```bash
npm test                         # All tests
npm test -- --coverage           # With coverage report
npm test -- StaticBlock          # Specific file
```

## API Base URLs

- Local: `http://localhost:8080/api`
- Staging: `https://api-staging.cbt-app.com/api`
- Production: `https://api.cbt-app.com/api`

## Database

```
PostgreSQL: cbt_platform
Tables:
  Core: users, courses, user_progress
  Editor: scenario_drafts, scenario_draft_versions, block_templates
  Future: sessions, check_ins, subscriptions, user_streaks,
          achievements, notifications
```

## Environment Variables

### Backend
```
DB_URL, DB_USERNAME, DB_PASSWORD
REDIS_HOST, REDIS_PORT
JWT_SECRET
CLAUDE_API_KEY
```

### Scenario Editor Frontend
```
VITE_API_URL=http://localhost:8080/api
```

### Mobile
```
API_URL
EXPO_PUBLIC_REVENUECAT_KEY
```

---

## Current Focus
✅ Реализация визуального редактора сценариев (Scenario Editor) для психологов/контент-мейкеров - **COMPLETED**

### Sprint Goal
✅ Реализовать MVP Scenario Editor (Backend + Frontend) с базовым CRUD, валидацией и публикацией в Course - **COMPLETED**

### Working On
✅ User module - COMPLETED
✅ Common module - COMPLETED
✅ Security module - COMPLETED
✅ Config module - COMPLETED
✅ Course module - COMPLETED
✅ Progress module - COMPLETED
✅ Database migrations - COMPLETED (V1-V5)
✅ Editor Backend module - COMPLETED
  ├─ ✅ Entity layer (ScenarioDraft, ScenarioDraftVersion, BlockTemplate)
  ├─ ✅ Repository layer
  ├─ ✅ Service layer (ScenarioEditorService, ScenarioValidationService, BlockTemplateService)
  ├─ ✅ Controller layer (REST API)
  └─ ✅ Database migration (V5)
✅ Editor Frontend (Web App) - COMPLETED
  ├─ ✅ Project setup (React + Vite + TypeScript + Material-UI)
  ├─ ✅ API services layer (auth, drafts, templates)
  ├─ ✅ State management (Zustand stores)
  ├─ ✅ Authentication & routing
  ├─ ✅ Pages: Login, Drafts list, Editor, Templates
  ├─ ✅ Layout with sidebar navigation
  └─ ✅ Validation & publishing UI

⏭️ Next: Engine и LLM modules для обработки сценариев

### Blockers
None - Editor MVP ready for testing

---

## Module Status

### Backend
| Module | Entity | Repo | Service | Controller | Tests |
|--------|--------|------|---------|------------|-------|
| user | ✅ | ✅ | ✅ | ✅ (Auth + User) | ❌ |
| common | ✅ (BaseException, ErrorResponse) | - | - | ✅ (GlobalExceptionHandler) | ❌ |
| config | - | - | - | ✅ (Security, Jackson) | ❌ |
| security | - | - | ✅ (JWT Provider, UserDetailsService, Filter) | - | ❌ |
| course | ✅ | ✅ | ✅ | ✅ | ❌ |
| progress | ✅ (UserProgress + userData JSONB) | ✅ | ✅ | ✅ | ❌ |
| editor | ✅ (Draft, Version, Template) | ✅ | ✅ (Editor, Validation, Template) | ✅ (2 controllers) | ❌ |
| session | ❌ | ❌ | ❌ | ❌ | ❌ |
| checkin | ❌ | ❌ | ❌ | ❌ | ❌ |
| engine | ❌ | - | ❌ | - | ❌ |
| llm | - | - | ❌ | - | ❌ |

**Completed Infrastructure:**
- ✅ User authentication (register, login with JWT)
- ✅ JWT-based security with access & refresh tokens
- ✅ BCrypt password hashing
- ✅ Global exception handling
- ✅ CORS configuration
- ✅ Spring Security integration
- ✅ Jackson JSON configuration for JSONB
- ✅ Application configuration (application.yml)
- ✅ Main Application class

**Completed Modules:**
- ✅ **Course module** - Full CRUD for courses with JSONB scenario storage
  - Load scenario from JSON file: `POST /api/courses/load-scenario`
  - Create/Update/Delete courses (admin only)
  - Public endpoints for course listing and details
  - Category filtering
  - Soft delete support

**Completed Modules:**
- ✅ **Progress module** - User progress tracking with JSONB userData
  - UserProgress entity with userData JSONB field
  - Start/update/complete course tracking
  - Session and block completion tracking
  - User data merge/replace functionality
  - Full REST API for progress management

- ✅ **Editor module** - Visual scenario editor for content creators
  - ScenarioDraft, ScenarioDraftVersion, BlockTemplate entities
  - Full CRUD for drafts with version history
  - Scenario validation service (structure validation)
  - Draft publishing to courses
  - Block template library for quick building
  - REST API endpoints:
    - `/api/editor/drafts` - Draft management (CRUD, validate, publish, versions)
    - `/api/editor/templates/blocks` - Block template management
  - Role-based access (ADMIN, EDITOR)

**Database:**
- ✅ **Flyway migrations created**:
  - V1: users table (email, password_hash, role, soft delete)
  - V2: courses table (slug, scenario_json JSONB, version, pricing)
  - V3: seed admin user (admin@cbt.com / Admin123!)
  - V4: user_progress table (userData JSONB, completion tracking, foreign keys)
  - V5: editor tables (scenario_drafts, scenario_draft_versions, block_templates)
- ✅ **Application compiles successfully**
- ✅ **TEST_API.md** created with API testing guide

**Next Steps:**
1. Start PostgreSQL and run application
2. Test full flow: register → login → load scenario → start course
3. Implement Engine module (CourseEngine + BlockHandlers)
4. Implement LLM module (Claude API integration)

### Scenario Editor Frontend (Web)
| Module | Types | Service | Store | Pages | Components |
|--------|-------|---------|-------|-------|------------|
| auth | ✅ | ✅ | ✅ | ✅ (LoginPage) | ✅ (Layout) |
| drafts | ✅ | ✅ | ✅ | ✅ (DraftsPage, EditorPage) | ✅ |
| templates | ✅ | ✅ | ✅ | ✅ (TemplatesPage) | ✅ |
| api | ✅ | ✅ (Axios client + interceptors) | - | - | - |

**Status**: ✅ MVP Complete - Ready for testing

### Mobile Frontend (React Native)
| Module | Types | Service | Store | Screens | Components |
|--------|-------|---------|-------|---------|------------|
| auth | ✅ | ✅ | ✅ | ✅ | ✅ |
| course | ✅ | ⚠️ | ⚠️ | ⚠️ | ❌ |
| session | ⚠️ | ❌ | ❌ | ❌ | ❌ |
| blocks | ⚠️ | - | - | - | ❌ |
| checkin | ❌ | ❌ | ❌ | ❌ | ❌ |

Legend: ✅ Done | ⚠️ In Progress | ❌ Not Started

---

## Commands Reference

### Backend (from backend/ directory)
```bash
# Development
./mvnw spring-boot:run          # Run dev server (port 8080)
./mvnw clean install            # Build project
./mvnw test                     # Run all tests
./mvnw test -Dtest=ClassName    # Run specific test class

# Database
docker-compose up -d postgres   # Start PostgreSQL container
./mvnw flyway:migrate           # Run database migrations
./mvnw flyway:clean             # Clean database (DEV ONLY)

# Code Quality
./mvnw spotless:apply           # Format code
./mvnw verify                   # Run tests + checks
```

### Mobile (from mobile/ directory)
```bash
# Development
npx expo start                  # Start dev server
npx expo start --clear          # Clear cache and start
npx expo run:ios                # Build and run on iOS simulator
npx expo run:android            # Build and run on Android emulator

# Dependencies
npm install                     # Install dependencies
npx expo install package        # Install expo-compatible package

# Testing
npm test                        # Run Jest tests
npm run type-check              # Run TypeScript compiler
npm run lint                    # Run ESLint
```

### Git Workflow
```bash
# Feature branches
git checkout -b feat/MODULE-NAME    # New feature
git checkout -b fix/ISSUE-DESC      # Bug fix
git checkout -b refactor/AREA       # Refactoring

# Commits (follow conventional commits)
# Examples:
# feat(course): add course engine skeleton
# fix(auth): resolve token refresh issue
# refactor(session): extract block handlers to separate classes
```

---

*Last updated: December 2024*
