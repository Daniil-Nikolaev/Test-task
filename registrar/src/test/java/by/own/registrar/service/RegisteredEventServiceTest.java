package by.own.registrar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.own.registrar.dto.RegisteredEventResponse;
import by.own.registrar.mapper.EventMapper;
import by.own.registrar.model.RegisteredEvent;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.registrar.util.EventFilter;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class RegisteredEventServiceTest {

    @Mock
    private RegisteredEventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private RegisteredEventService service;

    @Test
    void getEvents_success() {
        //Arrange
        EventFilter filter = new EventFilter(
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-01-07T23:59:59Z"),
            EventType.USER_LOGIN,
            "generator1"
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp"));

        RegisteredEvent entity = RegisteredEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType(EventType.USER_LOGIN)
            .source("generator1")
            .createdAt(Instant.now())
            .description("Test")
            .build();

        RegisteredEventResponse response = new RegisteredEventResponse(
            entity.getEventId(),
            entity.getEventType(),
            entity.getSource(),
            entity.getCreatedAt(),
            entity.getDescription()
        );

        Page<RegisteredEvent> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);
        when(mapper.toRegisteredEventResponse(entity)).thenReturn(response);

        //Act
        Page<RegisteredEventResponse> result = service.getEvents(filter, pageable);

        //Assert
        verify(repository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(mapper, times(1)).toRegisteredEventResponse(entity);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }
}