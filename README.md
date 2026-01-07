Тестовое задание: распределённая система обработки событий с использованием Spring Boot, Kafka и Spring Batch.

## Архитектура
- **generator-1** и **generator-2** — два генератора событий.
    - Генерируют события по таймеру.
    - Сохраняют в свою БД.
    - Отправляют в Kafka топик `events`.
    - Получают подтверждения из топика `events-confirmed`.
    - Отмечают события как обработанные.
    - Предоставляют статистику `/event/stats`.

- **registrar** — сервис регистрации событий.
    - Использует Spring Batch для пакетной обработки сообщений из топика `events`.
    - Сохраняет события в свою БД.
    - Отправляет подтверждения в топик `events-confirmed`.
    - Предоставляет REST API `/event/info`.

- **sharedSources** — общий модуль с DTO,enums и ExceptionHandler.

## Запуск
- `docker compose build`
- `docker compose up`
