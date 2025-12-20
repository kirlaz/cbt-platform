# Claude Code Quick Start

## Установка

```bash
npm install -g @anthropic-ai/claude-code
```

## Структура проекта

```
cbt-platform/
├── CLAUDE.md                    # ← Главный контекст (обязательно)
├── backend/
│   ├── CONVENTIONS.md           # ← Конвенции backend
│   └── src/...
├── mobile/
│   ├── CONVENTIONS.md           # ← Конвенции frontend  
│   └── src/...
└── docs/
```

## Начало работы

```bash
# 1. Создать проект
mkdir cbt-platform && cd cbt-platform

# 2. Скопировать файлы
cp CLAUDE.md .
mkdir -p backend mobile
cp backend-CONVENTIONS.md backend/CONVENTIONS.md
cp mobile-CONVENTIONS.md mobile/CONVENTIONS.md

# 3. Инициализировать git
git init

# 4. Запустить Claude Code
claude
```

## Ежедневный workflow

```bash
# Утро
cd cbt-platform
claude

# Обновить фокус
> Сегодня работаем над CheckIn модулем. Обнови CLAUDE.md

# Работа
> Создай CheckIn entity по конвенциям
> Создай CheckInRepository
> Создай CheckInService
> Создай CheckInController
> Напиши тесты

# Коммит
> git commit -m "feat: add CheckIn module"
```

## Шаблоны запросов

### Backend: Новый модуль
```
Создай {Module} модуль:
1. Entity в backend/src/.../entity/
2. Repository с методами: [...]
3. DTO: Create{Module}Request, {Module}Response
4. Service (interface + impl)
5. Controller: GET, POST, PUT, DELETE

Используй конвенции из CONVENTIONS.md
```

### Frontend: Новый компонент
```
Создай {Name} компонент:
- Props: [...]
- UI: [описание]
- Анимации: [если нужны]

По конвенциям из mobile/CONVENTIONS.md
```

### Рефакторинг
```
В {файл/модуль}:
- Найди [проблему]
- Исправь по паттерну из CONVENTIONS.md
```

### Debugging
```
Ошибка: [текст ошибки]
Файл: [путь]

Найди причину и исправь.
```

## Полезные команды

```bash
# Интерактивный режим
claude

# Быстрый запрос
claude "объясни CourseEngine"

# С файлом
claude "добавь метод" -f path/to/file.java

# Без выполнения команд
claude --no-run
```

## Tips

1. **Обновляй CLAUDE.md** — Current Focus секция
2. **Работай итеративно** — один модуль за раз
3. **Проси review** — после генерации
4. **Коммить часто** — через Claude Code
5. **Используй конвенции** — ссылайся на CONVENTIONS.md

---

## Статус модулей (пример)

### Backend
```
✅ user      — готов
✅ course    — готов  
⚠️ session   — в работе
❌ checkin   — не начат
❌ engine    — не начат
```

### Frontend
```
✅ auth      — готов
⚠️ course    — в работе
❌ session   — не начат
❌ blocks    — не начат
```
