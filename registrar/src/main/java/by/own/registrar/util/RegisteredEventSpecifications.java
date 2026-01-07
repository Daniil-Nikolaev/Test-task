package by.own.registrar.util;

import by.own.registrar.model.RegisteredEvent;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public class RegisteredEventSpecifications {

    public static Specification<RegisteredEvent> withFilter(EventFilter filter) {
        return Specification
            .where(createdAfter(filter.from()))
            .and(createdBefore(filter.to()))
            .and(hasType(filter.eventType()))
            .and(hasSource(filter.source()));
    }

    private static Specification<RegisteredEvent> createdAfter(Instant from) {
        return (root, query, builder) -> {
            if (from == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("createdAt"), from);
        };
    }

    private static Specification<RegisteredEvent> createdBefore(Instant to) {
        return (root, query, builder) -> {
            if (to == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    private static Specification<RegisteredEvent> hasType(EventType type) {
        return (root, query, builder) -> {
            if (type == null) {
                return null;
            }
            return builder.equal(root.get("eventType"), type);
        };
    }

    private static Specification<RegisteredEvent> hasSource(String source) {
        return (root, query, builder) -> {
            if (source == null || source.isBlank()) {
                return null;
            }
            return builder.equal(root.get("source"), source);
        };
    }
}