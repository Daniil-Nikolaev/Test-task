package by.own.sharedsources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EventDlqMessage(
    @NotNull
    UUID eventId,

    @NotBlank
    String sourceService,

    @NotBlank
    String errorMessage,

    @NotNull
    Instant failedAt
) {}