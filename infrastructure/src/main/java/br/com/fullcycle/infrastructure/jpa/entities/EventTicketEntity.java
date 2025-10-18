package br.com.fullcycle.infrastructure.jpa.entities;

import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventTicket;
import br.com.fullcycle.domain.event.EventTicketId;
import br.com.fullcycle.domain.event.ticket.TicketId;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketEntity {

    @Id
    private UUID eventTicketId;

    private UUID ticketId;

    private UUID customerId;

    private int ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventEntity event;

    public EventTicketEntity() {
    }

    public EventTicketEntity(
            final UUID eventTicketId,
            final UUID customerId,
            final int ordering,
            final UUID ticketId,
            final EventEntity event
    ) {
        this.eventTicketId = eventTicketId;
        this.customerId = customerId;
        this.ordering = ordering;
        this.ticketId = ticketId;
        this.event = event;
    }

    public static EventTicketEntity of(final EventEntity event, final EventTicket eventTicket) {
        return new EventTicketEntity(
                UUID.fromString(eventTicket.getEventTicketId().value()),
                UUID.fromString(eventTicket.getCustomerId().value()),
                eventTicket.getOrdering(),
                eventTicket.getTicketId() != null ? UUID.fromString(eventTicket.getTicketId().value()): null,
                event
        );
    }

    public EventTicket toEventTicket() {
        return new EventTicket(
                EventTicketId.with(this.eventTicketId.toString()),
                EventId.with(this.event.getId().toString()),
                CustomerId.with(this.customerId.toString()),
                this.ticketId != null ? TicketId.with(this.ticketId.toString()) : null,
                this.ordering
        );
    }

    public UUID getEventTicketId() {
        return eventTicketId;
    }

    public void setEventTicketId(UUID eventTicketId) {
        this.eventTicketId = eventTicketId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventTicketEntity that = (EventTicketEntity) o;
        return Objects.equals(eventTicketId, that.eventTicketId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventTicketId);
    }
}
