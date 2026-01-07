package by.own.generator1.consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import by.own.generator1.model.Event;
import by.own.generator1.repository.EventRepository;
import by.own.sharedsources.dto.EventConfirmedMessage;
import by.own.sharedsources.model.ConfirmationStatus;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
public class EventConfirmationConsumerTest {

    @Mock
    private EventRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private EventConfirmationConsumer consumer;

    @Test
    void handleEventConfirmation_success() {
        //Arrange
        Event event = Event.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_CREATED)
            .source("generator1")
            .createdAt(Instant.now())
            .build();

        EventConfirmedMessage message = EventConfirmedMessage.builder()
            .eventId(event.getEventId())
            .source("generator1")
            .status(ConfirmationStatus.SUCCESS)
            .build();

        when(repository.findById(event.getEventId())).thenReturn(Optional.of(event));

        //Act
        consumer.handleEventConfirmation(message, ack);

        //Assert
        verify(repository, times(1)).findById(event.getEventId());
        verify(repository, times(1)).save(event);
        assertThat(event.isConfirmed()).isTrue();
        verify(ack, times(1)).acknowledge();
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void handleEventConfirmation_ifConfirmed_doesNotSaveAgain() {
        //Arrange
        Event event = Event.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_CREATED)
            .source("generator1")
            .createdAt(Instant.now())
            .isConfirmed(true)
            .build();

        EventConfirmedMessage message = EventConfirmedMessage.builder()
            .eventId(event.getEventId())
            .source("generator1")
            .status(ConfirmationStatus.SUCCESS)
            .build();

        when(repository.findById(event.getEventId())).thenReturn(Optional.of(event));

        //Act
        consumer.handleEventConfirmation(message, ack);

        //Assert
        verify(repository, times(1)).findById(event.getEventId());
        verify(repository, never()).save(any());
        verify(ack, times(1)).acknowledge();
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void handleEventConfirmation_eventNotFound() {
        //Arrange
        EventConfirmedMessage message = EventConfirmedMessage.builder()
            .eventId(UUID.randomUUID())
            .source("generator1")
            .status(ConfirmationStatus.SUCCESS)
            .build();

        when(repository.findById(message.eventId())).thenReturn(Optional.empty());

        //Act
        consumer.handleEventConfirmation(message, ack);

        //Assert
        verify(repository, times(1)).findById(message.eventId());
        verify(repository, never()).save(any());
        verify(ack, times(1)).acknowledge();
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void handleEventConfirmation_failedStatus() {
        //Arrange
        EventConfirmedMessage message = EventConfirmedMessage.builder()
            .eventId(UUID.randomUUID())
            .source("generator1")
            .status(ConfirmationStatus.FAILED)
            .build();

        //Act
        consumer.handleEventConfirmation(message, ack);

        //Assert
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
        verify(ack, times(1)).acknowledge();
        verifyNoInteractions(kafkaTemplate);
    }
}