package by.own.sharedsources.dto;

import by.own.sharedsources.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EventCreatedMessage (
    @NotNull
    UUID eventId,

    @NotNull
    EventType eventType,

    @NotBlank
    String source,

    @NotNull
    Instant createdAt,

    String description
){}