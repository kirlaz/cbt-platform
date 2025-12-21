# Git — ежедневный набор команд для одного разработчика

Этот файл — практическая шпаргалка по Git для **одиночной разработки**. Покрывает ~95% повседневных сценариев: от начала дня до завершения задачи и восстановления после ошибок.

---

## 1. Старт дня: привести рабочую копию в порядок

```bash
git status
git checkout main
git pull --rebase
```

Если есть незакоммиченные изменения и нужно переключиться:

```bash
git stash -u
git checkout main
git pull --rebase
git stash pop
```

---

## 2. Начать задачу: создать ветку под работу

```bash
git checkout -b work/<short-task-name>
```

Пример:

```bash
git checkout -b work/add-npv
```

---

## 3. В процессе работы: смотреть изменения

```bash
git status
git diff
git diff --staged
```

---

## 4. Добавлять изменения в коммит аккуратно

Чаще всего:

```bash
git add .
```

Аккуратно по кускам (рекомендуется):

```bash
git add -p
```

Убрать файл из stage:

```bash
git restore --staged <file>
```

---

## 5. Делать коммит

Обычный коммит:

```bash
git commit -m "Краткое осмысленное описание"
```

Поправить последний коммит (сообщение или забытые файлы):

```bash
git add <file>
git commit --amend
```

---

## 6. Синхронизация с удалённым репозиторием

Отправить ветку:

```bash
git push -u origin work/<short-task-name>
```

Подтянуть изменения без merge-коммита:

```bash
git pull --rebase
```

---

## 7. Завершить задачу: слить в main и убрать ветку

Если вы работаете один:

```bash
git checkout main
git pull --rebase
git merge --no-ff work/<short-task-name>
git push
git branch -d work/<short-task-name>
```

Удалить ветку на origin (если публиковалась):

```bash
git push origin --delete work/<short-task-name>
```

---

## 8. Быстрые «спасательные» команды

### Временно отложить изменения

```bash
git stash -u
git stash list
git stash pop
```

### Отменить правки в файле

```bash
git restore <file>
```

### Убрать всё из stage, но оставить изменения

```bash
git restore --staged .
```

### Откатить последний коммит

Мягко (изменения сохраняются):

```bash
git reset --soft HEAD~1
```

Жёстко (изменения удаляются):

```bash
git reset --hard HEAD~1
```

### Найти коммит, где сломалось

```bash
git bisect start
git bisect bad
git bisect good <hash>
# помечайте good/bad после проверки
git bisect reset
```

---

## 9. Полезные настройки (делаются один раз)

Всегда использовать rebase при pull:

```bash
git config --global pull.rebase true
```

Удобный alias для лога:

```bash
git config --global alias.lg "log --oneline --graph --decorate --all"
```

Использование:

```bash
git lg
```

---

## 10. Минимальный ежедневный сценарий

```bash
git checkout main
git pull --rebase
git checkout -b work/task
# ... changes ...
git add -p
git commit -m "..."
git push -u origin work/task
```

---

**Принцип:**
> Коммитьте часто, экспериментируйте в ветках, `main` держите в рабочем состоянии.

