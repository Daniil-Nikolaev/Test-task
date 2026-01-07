package by.own.generator2.service;

import by.own.generator2.mapper.EventMapper;
import by.own.generator2.model.Event;
import by.own.generator2.properties.EventGeneratorProperties;
import by.own.generator2.repository.EventRepository;
import by.own.sharedsources.dto.EventCreatedMessage;
import by.own.sharedsources.dto.EventDlqMessage;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventGeneratorService {

    private static final String SEND_EVENT = "events";
    private static final String SEND_DLQ = "events-dlq";

    private final EventGeneratorProperties props;
    private final EventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventMapper eventMapper;

    @Scheduled(fixedRateString = "${app.generate-interval-ms}")
    public void generateEvent() {
        Event event = Event.builder()
            .eventType(randomEventType())
            .source(props.getServiceId())
            .build();

        Event saved = repository.save(event);
        sendMessage(saved);
    }

    private EventType randomEventType() {
        EventType[] values = EventType.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    private void sendMessage(Event event) {
        EventCreatedMessage message = eventMapper.toEventCreatedMessage(event);

        kafkaTemplate.send(SEND_EVENT, message)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Событие отправлено: id={}, topic={}", event.getEventId(), SEND_EVENT);
                } else {
                    log.error("Не удалось отправить событие: id={}", event.getEventId(), ex);
                    EventDlqMessage dlqMessage = new EventDlqMessage(
                        event.getEventId(),
                        event.getSource(),
                        ex.getMessage(),
                        Instant.now()
                    );
                    kafkaTemplate.send(SEND_DLQ, dlqMessage);
                }
            });
    }
}