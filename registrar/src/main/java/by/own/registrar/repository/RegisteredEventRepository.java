package by.own.registrar.repository;

import by.own.registrar.model.RegisteredEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegisteredEventRepository extends JpaRepository<RegisteredEvent, UUID>,
    JpaSpecificationExecutor<RegisteredEvent> {}