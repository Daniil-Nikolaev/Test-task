package by.own.generator2.service;

import by.own.generator2.repository.EventRepository;
import by.own.sharedsources.dto.EventStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventStatsResponse getStats() {
        log.info("Запрос статистики генератора-2");

        EventStatsResponse response = new EventStatsResponse(
            eventRepository.count(),
            eventRepository.countByIsConfirmedTrue(),
            eventRepository.countByIsConfirmedFalse()
        );

        log.info("Статистика генератора-2: {}", response);
        return response;
    }
}