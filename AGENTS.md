# Repository Guidelines

## Project Structure & Module Organization
- `backend/`: Java Spring Boot API. Source in `backend/src/main/java/com/cbt/platform/` with module folders like `user/`, `course/`, `progress/`, `engine/`, and `llm/`.
- `backend/src/main/resources/`: Flyway migrations in `db/migration/`, scenario JSON in `scenarios/`.
- `mobile/`: React Native + Expo app. Source in `mobile/src/` with `screens/`, `components/blocks/`, `services/`, `store/`, `hooks/`, `types/`, `constants/`.
- Tests: `backend/src/test/java/...` (unit, integration, e2e) and `mobile/src/__tests__/` plus `mobile/src/components/blocks/__tests__/`.

## Build, Test, and Development Commands
Run from the module directory.
- Backend: `./mvnw spring-boot:run` (dev server), `./mvnw clean install` (build), `./mvnw test` (tests), `./mvnw verify` (tests + checks), `./mvnw spotless:apply` (format), `docker-compose up -d postgres` and `./mvnw flyway:migrate` (DB).
- Mobile: `npm install` (deps), `npx expo start` (dev), `npm test` (Jest), `npm run lint` (ESLint), `npm run type-check` (TS).

## Coding Style & Naming Conventions
- Backend: follow package structure `entity/`, `repository/`, `service/`, `controller/`, `dto/`, `mapper/`, `exception/`. Use `UUID` ids, JSONB via `JsonNode`, Lombok, and MapStruct. Naming: `CourseService`/`CourseServiceImpl`, `CourseController`, `CreateCourseRequest`, `CourseMapper`.
- Mobile: TypeScript with `interface` for props, `React.FC`, `StyleSheet.create()`, named exports. Naming: `{Name}Screen.tsx`, `use{Name}Store.ts`, `{name}Service.ts`.
- Formatting: rely on Spotless and ESLint; keep changes consistent with existing files.

## Testing Guidelines
- Backend: JUnit 5, Mockito, Spring Boot Test, Testcontainers. Naming pattern: `shouldDoSomethingWhenCondition()`. Coverage goals: 80% unit, 60% integration; critical paths 100%.
- Mobile: Jest + React Testing Library + jest-expo. Prefer behavior-focused assertions and avoid snapshots. Coverage goals: 70% components, 80% utils/stores.

## Commit & Pull Request Guidelines
- No local Git history detected. Follow conventional commits from `CLAUDE.md`: `feat(scope): ...`, `fix(scope): ...`, `refactor(scope): ...`.
- Branches: `feat/`, `fix/`, `refactor/`.
- PRs: include a concise summary, test commands run, and screenshots for UI changes. Link relevant issues.

## Security & Configuration Tips
- Backend env: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `JWT_SECRET`, `CLAUDE_API_KEY`.
- Mobile env: `API_URL`, `EXPO_PUBLIC_REVENUECAT_KEY`.
- Do not commit secrets; use local env files or secret managers.
