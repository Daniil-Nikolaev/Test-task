package by.own.registrar.batch;

import by.own.registrar.mapper.EventMapper;
import by.own.registrar.model.RegisteredEvent;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.sharedsources.dto.EventCreatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventItemProcessor implements ItemProcessor<EventCreatedMessage, RegisteredEvent> {

    private final RegisteredEventRepository repository;
    private final EventMapper mapper;

    @Override
    public RegisteredEvent process(EventCreatedMessage message) {
        if (repository.existsById(message.eventId())) {
            log.debug("Событие уже существует: {}", message.eventId());
            return null;
        }
        return mapper.toRegisteredEvent(message);
    }
}