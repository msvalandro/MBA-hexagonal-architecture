package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.ticket.TicketId;

import java.time.Instant;
import java.util.UUID;

public record TicketCreated(
        String domainEventId,
        String type,
        String ticketId,
        String eventTicketId,
        String eventId,
        String customerId,
        Instant occurredOn
) implements DomainEvent {

    public TicketCreated(TicketId ticketId, EventTicketId eventTicketId, EventId eventId, CustomerId customerId) {
        this(
                UUID.randomUUID().toString(),
                "ticket.created",
                ticketId.value(),
                eventTicketId.value(),
                eventId.value(),
                customerId.value(),
                Instant.now()
        );
    }
}
