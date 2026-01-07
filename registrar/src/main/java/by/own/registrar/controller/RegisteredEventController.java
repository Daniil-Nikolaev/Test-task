package by.own.registrar.controller;

import by.own.registrar.dto.RegisteredEventResponse;
import by.own.registrar.service.RegisteredEventService;
import by.own.registrar.util.EventFilter;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class RegisteredEventController {

    private final RegisteredEventService service;

    @GetMapping("/info")
    public ResponseEntity<Page<RegisteredEventResponse>> getEvents(
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(required = false) EventType type,
        @RequestParam(required = false) String source,
        Pageable pageable) {

        EventFilter filter = new EventFilter(from, to, type, source);
        Page<RegisteredEventResponse> result = service.getEvents(filter, pageable);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(result);
    }
}