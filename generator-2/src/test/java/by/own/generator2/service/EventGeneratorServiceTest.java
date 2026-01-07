package by.own.generator2.service;

import static org.mockito.Mockito.*;

import by.own.generator2.model.Event;
import by.own.generator2.properties.EventGeneratorProperties;
import by.own.generator2.repository.EventRepository;
import by.own.generator2.mapper.EventMapper;
import by.own.sharedsources.dto.EventCreatedMessage;
import by.own.sharedsources.model.EventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class EventGeneratorServiceTest {

    @Mock
    private EventGeneratorProperties properties;

    @Mock
    private EventRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventGeneratorService generatorService;

    @Test
    void generateEvent_savesEventAndSendsMessage() {
        //Arrange
        Event event = Event.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_CREATED)
            .source("generator2")
            .createdAt(Instant.now())
            .build();

        EventCreatedMessage message = EventCreatedMessage.builder()
            .eventId(event.getEventId())
            .eventType(event.getEventType())
            .source(event.getSource())
            .createdAt(event.getCreatedAt())
            .build();

        when(repository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toEventCreatedMessage(any(Event.class))).thenReturn(message);
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        //Act
        generatorService.generateEvent();

        //Assert
        verify(repository, times(1)).save(any(Event.class));
        verify(kafkaTemplate, times(1)).send("events", message);
    }
}