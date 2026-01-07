package by.own.generator1.service;

import by.own.generator1.repository.EventRepository;
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
        log.info("Запрос статистики генератора-1");

        EventStatsResponse response = new EventStatsResponse(
            eventRepository.count(),
            eventRepository.countByIsConfirmedTrue(),
            eventRepository.countByIsConfirmedFalse()
        );

        log.info("Статистика генератора-1: {}", response);
        return response;
    }
}