package by.own.registrar.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import by.own.registrar.mapper.EventMapper;
import by.own.registrar.model.RegisteredEvent;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.sharedsources.dto.EventCreatedMessage;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventItemProcessorTest {

    @Mock
    private RegisteredEventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventItemProcessor processor;

    @Test
    void process_success() {
        //Arrange
        EventCreatedMessage message = EventCreatedMessage.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_LOGIN)
            .source("generator1")
            .createdAt(Instant.now())
            .description("Test")
            .build();

        RegisteredEvent registeredEvent = RegisteredEvent.builder()
            .eventId(message.eventId())
            .eventType(message.eventType())
            .source(message.source())
            .createdAt(message.createdAt())
            .description(message.description())
            .build();

        when(repository.existsById(message.eventId())).thenReturn(false);
        when(mapper.toRegisteredEvent(message)).thenReturn(registeredEvent);

        //Act
        RegisteredEvent result = processor.process(message);

        //Assert
        verify(repository, times(1)).existsById(message.eventId());
        verify(mapper, times(1)).toRegisteredEvent(message);
        verifyNoMoreInteractions(repository, mapper);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(registeredEvent);
    }

    @Test
    void process_ifDuplicate() {
        //Arrange
        EventCreatedMessage message = EventCreatedMessage.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_LOGIN)
            .source("generator1")
            .createdAt(Instant.now())
            .description("Test")
            .build();

        when(repository.existsById(message.eventId())).thenReturn(true);

        //Act
        RegisteredEvent result = processor.process(message);

        //Assert
        verify(repository, times(1)).existsById(message.eventId());
        verifyNoMoreInteractions(repository, mapper);

        assertThat(result).isNull();
    }
}