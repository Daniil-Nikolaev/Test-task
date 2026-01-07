package by.own.registrar.mapper;

import by.own.registrar.dto.RegisteredEventResponse;
import by.own.registrar.model.RegisteredEvent;
import by.own.sharedsources.dto.EventConfirmedMessage;
import by.own.sharedsources.dto.EventCreatedMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    RegisteredEvent toRegisteredEvent(EventCreatedMessage message);

    @Mapping(target = "confirmedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "status", constant = "SUCCESS")
    EventConfirmedMessage toEventConfirmedMessage(RegisteredEvent message);

    RegisteredEventResponse toRegisteredEventResponse(RegisteredEvent event);
}