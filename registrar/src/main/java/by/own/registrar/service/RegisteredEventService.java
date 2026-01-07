package by.own.registrar.service;

import by.own.registrar.dto.RegisteredEventResponse;
import by.own.registrar.mapper.EventMapper;
import by.own.registrar.repository.RegisteredEventRepository;
import by.own.registrar.util.EventFilter;
import by.own.registrar.util.RegisteredEventSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisteredEventService {

    private final RegisteredEventRepository repository;
    private final EventMapper mapper;

    public Page<RegisteredEventResponse> getEvents(EventFilter filter, Pageable pageable) {
        log.info("Запрос информации о зарегистрированных событиях");

        return repository
            .findAll(RegisteredEventSpecifications.withFilter(filter), pageable)
            .map(mapper::toRegisteredEventResponse);
    }
}