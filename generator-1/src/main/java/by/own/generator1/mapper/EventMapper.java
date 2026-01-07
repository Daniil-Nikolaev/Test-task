package by.own.generator1.mapper;

import by.own.generator1.model.Event;
import by.own.sharedsources.dto.EventCreatedMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "description", expression = "java(event.getEventType().getDescription())")
    EventCreatedMessage toEventCreatedMessage(Event event);
}