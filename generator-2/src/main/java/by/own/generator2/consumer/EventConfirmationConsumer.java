package by.own.generator2.consumer;

import by.own.generator2.repository.EventRepository;
import by.own.sharedsources.dto.EventConfirmedMessage;
import by.own.sharedsources.dto.EventDlqMessage;
import by.own.sharedsources.model.ConfirmationStatus;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConfirmationConsumer {

    private static final String RECEIVE_EVENT = "events-confirmed";
    private static final String RECEIVE_DLQ = "events-confirmed-dlq";

    private final EventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = RECEIVE_EVENT)
    public void handleEventConfirmation(@Payload EventConfirmedMessage message, Acknowledgment ack) {
        try {
            if (!isSuccessful(message)) {
                ack.acknowledge();
                return;
            }
            confirmation(message);

            ack.acknowledge();
        } catch (Exception e) {
            handleProcessingError(message, e);
            ack.acknowledge();
        }
    }

    private boolean isSuccessful(EventConfirmedMessage message) {
        if (message.status() != ConfirmationStatus.SUCCESS) {
            log.warn("Неуспешное подтверждение: eventId={}, status={}", message.eventId(), message.status());
            return false;
        }
        return true;
    }

    private void confirmation(EventConfirmedMessage message){
        repository.findById(message.eventId())
            .ifPresentOrElse(e -> {
                if (!e.isConfirmed()) {
                    e.setConfirmed(true);
                    repository.save(e);
                    log.info("Событие подтверждено: id={}", e.getEventId());
                } else {
                    log.debug("Событие уже было подтверждено: id={}", e.getEventId());
                }
            }, () -> log.warn("Событие не найдено в БД: id={}", message.eventId()));
    }

    private void handleProcessingError(EventConfirmedMessage message, Exception e) {
        log.error("Ошибка обработки подтверждения: eventId={}", message.eventId(), e);

        EventDlqMessage dlqMessage = new EventDlqMessage(
            message.eventId(),
            message.source(),
            e.getMessage(),
            Instant.now()
        );
        kafkaTemplate.send(RECEIVE_DLQ, dlqMessage);
    }
}