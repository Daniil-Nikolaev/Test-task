package by.own.registrar.batch;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import by.own.registrar.mapper.EventMapper;
import by.own.registrar.model.RegisteredEvent;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.sharedsources.dto.EventConfirmedMessage;
import by.own.sharedsources.model.ConfirmationStatus;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.batch.infrastructure.item.Chunk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
public class EventItemWriterTest {

    @Mock
    private RegisteredEventRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventItemWriter writer;

    @Test
    void write_success() {
        //Arrange
        RegisteredEvent event1 = RegisteredEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_LOGIN)
            .source("generator1")
            .createdAt(Instant.now())
            .description("Test1")
            .build();

        RegisteredEvent event2 = RegisteredEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_LOGOUT)
            .source("generator2")
            .createdAt(Instant.now())
            .description("Test2")
            .build();

        EventConfirmedMessage message1 = EventConfirmedMessage.builder()
            .eventId(event1.getEventId())
            .source(event1.getSource())
            .confirmedAt(Instant.now())
            .status(ConfirmationStatus.SUCCESS)
            .build();

        EventConfirmedMessage message2 = EventConfirmedMessage.builder()
            .eventId(event2.getEventId())
            .source(event2.getSource())
            .confirmedAt(Instant.now())
            .status(ConfirmationStatus.SUCCESS)
            .build();

        CompletableFuture future1 = CompletableFuture.completedFuture(mock(SendResult.class));
        CompletableFuture future2 = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq("events-confirmed"), eq(message1))).thenReturn(future1);
        when(kafkaTemplate.send(eq("events-confirmed"), eq(message2))).thenReturn(future2);

        when(mapper.toEventConfirmedMessage(event1)).thenReturn(message1);
        when(mapper.toEventConfirmedMessage(event2)).thenReturn(message2);

        Chunk<RegisteredEvent> chunk = new Chunk<>(List.of(event1, event2));

        //Act
        writer.write(chunk);

        //Assert
        verify(repository, times(1)).save(event1);
        verify(repository, times(1)).save(event2);
        verify(mapper, times(1)).toEventConfirmedMessage(event1);
        verify(mapper, times(1)).toEventConfirmedMessage(event2);
        verify(kafkaTemplate, times(1)).send("events-confirmed", message1);
        verify(kafkaTemplate, times(1)).send("events-confirmed", message2);

        verifyNoMoreInteractions(repository, mapper, kafkaTemplate);
    }
}