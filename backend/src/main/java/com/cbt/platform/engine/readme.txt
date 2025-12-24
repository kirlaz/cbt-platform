 1. Базовые структуры данных

  - BlockType enum - 12 типов блоков (STATIC, INPUT, SLIDER, SINGLE_SELECT, MULTI_SELECT, LLM_CONVERSATION, LLM_RESPONSE, EXERCISE, VISUALIZATION, CALCULATION, SESSION_COMPLETE, PAYWALL)
  - BlockResult - результат обработки блока
  - BlockInputRequest - DTO для пользовательского ввода

  2. BlockHandler interface и 12 реализаций

  Создан интерфейс BlockHandler и реализации для каждого типа блока:

  Полностью функциональные:
  - ✅ StaticBlockHandler - статический контент
  - ✅ InputBlockHandler - текстовый ввод с валидацией
  - ✅ SliderBlockHandler - числовой слайдер
  - ✅ SingleSelectBlockHandler - выбор одной опции (поддерживает conditional navigation)
  - ✅ MultiSelectBlockHandler - выбор нескольких опций
  - ✅ SessionCompleteBlockHandler - завершение сессии
  - ✅ PaywallBlockHandler - paywall для подписки

  Stub handlers (готовы к расширению):
  - ⚠️ LlmConversationBlockHandler - чат с AI (требует интеграции с LLM модулем)
  - ⚠️ LlmResponseBlockHandler - генерация ответов AI
  - ⚠️ ExerciseBlockHandler - CBT упражнения
  - ⚠️ VisualizationBlockHandler - визуализация данных
  - ⚠️ CalculationBlockHandler - вычисления (например, GAD-7 score)

  3. BlockHandlerRegistry (Strategy Pattern)

  - Автоматическая регистрация всех handlers через Spring DI
  - Маппинг BlockType → Handler
  - Валидация наличия handlers для всех типов блоков

  4. CourseEngine Service (Orchestrator)

  Ключевой компонент, который:
  - ✅ Загружает сценарий из Course
  - ✅ Определяет текущий блок пользователя из UserProgress
  - ✅ Использует BlockHandlerRegistry для обработки блоков
  - ✅ Обновляет UserProgress и userData после обработки
  - ✅ Управляет навигацией между блоками и сессиями
  - ✅ Обрабатывает пользовательский ввод с валидацией

  5. SessionController (REST API)

  Endpoints для взаимодействия с курсом:
  - GET /api/sessions/courses/{courseId}/current-block - получить текущий блок
  - POST /api/sessions/courses/{courseId}/submit-block - отправить ответ пользователя
  - POST /api/sessions/courses/{courseId}/next-block - перейти к следующему блоку

  6. UserProgressRepository

  - Создан repository для работы с прогрессом пользователя
  - Поддержка запросов по userId и courseId

  7. Exception handling

  - BlockNotFoundException - блок не найден в сценарии
  - SessionNotFoundException - сессия не найдена в курсе

  🏗️ Архитектура

  CourseEngine (orchestrator)
      ↓
  BlockHandlerRegistry (strategy pattern)
      ↓
  BlockHandler implementations (12 handlers)
      ↓
  BlockResult → UserProgress update

  📋 REST API Flow

  1. User starts course → UserProgress created
  2. GET /current-block → Returns first block (STATIC welcome message)
  3. POST /next-block → Move to next block (INPUT - collect name)
  4. POST /submit-block → Process input, save to userData, move forward
  5. ... continue through all blocks in session
  6. SESSION_COMPLETE block → Session marked complete
  7. Auto-navigate to next session