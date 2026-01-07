package by.own.generator2.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class EventGeneratorProperties {

    @NotBlank
    private String serviceId;

    @NotNull
    private long generateIntervalMs;
}