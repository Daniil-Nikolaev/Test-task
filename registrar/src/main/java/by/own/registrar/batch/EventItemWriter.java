package by.own.registrar.batch;

import by.own.registrar.mapper.EventMapper;
import by.own.registrar.model.RegisteredEvent;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.sharedsources.dto.EventConfirmedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventItemWriter implements ItemWriter<RegisteredEvent> {

    private static final String CONFIRM_TOPIC = "events-confirmed";

    private final RegisteredEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventMapper mapper;

    @Override
    @Transactional
    public void write(Chunk<? extends RegisteredEvent> chunk) {
        for (RegisteredEvent event : chunk.getItems()) {
            try {
                repository.save(event);

                EventConfirmedMessage message = mapper.toEventConfirmedMessage(event);

                kafkaTemplate.send(CONFIRM_TOPIC, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Не удалось отправить подтверждение: eventId={}", event.getEventId(), ex);
                        } else {
                            log.info("Подтверждение отправлено: eventId={}", event.getEventId());
                        }
                    });
            } catch (Exception e) {
                log.error("Ошибка при записи события: eventId={}", event.getEventId(), e);
                throw e;
            }
        }
    }
}