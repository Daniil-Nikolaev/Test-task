Тестовое задание: распределённая система обработки событий с использованием Spring Boot, Kafka и Spring Batch.

## Архитектура
- **generator-1** и **generator-2** — два генератора событий.
    - Генерируют события по таймеру.
    - Сохраняют в свою БД.
    - Отправляют в Kafka топик `events`.
    - Получают подтверждения из топика `events-confirmed`.
    - Отмечают события как обработанные после подтверждения.
    - Предоставляют статистику `/event/stats`.

- **registrar** — сервис регистрации событий.
    - Использует Spring Batch для пакетной обработки сообщений из топика `events`.
    - Сохраняет события в свою БД.
    - Отправляет подтверждения в топик `events-confirmed`.
    - Предоставляет REST API `/event/info`. Примеры запросов:
      - `/event/info?type=USER_LOGIN&source=generator2`
      - `/event/info?from=2026-01-01T00:00:00Z&to=2026-01-06T23:59:59Z&type=USER_LOGIN`
      - `/event/info?page=0&size=20`

- **sharedSources** — общий модуль с DTO,enums и ExceptionHandler.

## Запуск
- `docker compose build`
- `docker compose up`
