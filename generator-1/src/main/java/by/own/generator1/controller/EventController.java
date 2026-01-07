package by.own.generator1.controller;

import by.own.generator1.service.EventService;
import by.own.sharedsources.dto.EventStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/stats")
    public ResponseEntity<EventStatsResponse> getStats() {
        return ResponseEntity.ok().body(eventService.getStats());
    }
}