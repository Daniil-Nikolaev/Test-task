package by.own.registrar.util;

import by.own.sharedsources.model.EventType;
import java.time.Instant;
import lombok.Builder;

@Builder
public record EventFilter(
    Instant from,
    Instant to,
    EventType eventType,
    String source
) {}