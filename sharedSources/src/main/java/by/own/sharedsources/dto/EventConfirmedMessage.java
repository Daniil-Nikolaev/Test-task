package by.own.sharedsources.dto;

import by.own.sharedsources.model.ConfirmationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EventConfirmedMessage (
    @NotNull
    UUID eventId,

    @NotBlank
    String source,

    @NotNull
    Instant confirmedAt,

    @NotNull
    ConfirmationStatus status
){}