package by.own.generator2.repository;

import by.own.generator2.model.Event;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    long count();

    long countByIsConfirmedTrue();

    long countByIsConfirmedFalse();
}