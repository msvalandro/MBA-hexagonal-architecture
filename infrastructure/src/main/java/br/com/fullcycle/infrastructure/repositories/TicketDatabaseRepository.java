package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import br.com.fullcycle.infrastructure.jpa.entities.EventEntity;
import br.com.fullcycle.infrastructure.jpa.entities.OutboxEntity;
import br.com.fullcycle.infrastructure.jpa.entities.TicketEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.OutboxJpaRepository;
import br.com.fullcycle.infrastructure.jpa.repositories.TicketJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

// Interface Adapter
@Component
public class TicketDatabaseRepository implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper mapper;

    public TicketDatabaseRepository(
            final TicketJpaRepository ticketJpaRepository,
            final OutboxJpaRepository outboxJpaRepository,
            final ObjectMapper mapper
    ) {
        this.ticketJpaRepository = Objects.requireNonNull(ticketJpaRepository);
        this.outboxJpaRepository = Objects.requireNonNull(outboxJpaRepository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public Optional<Ticket> ticketOfId(final TicketId anId) {
        Objects.requireNonNull(anId, "ID cannot be null");

        return this.ticketJpaRepository.findById(UUID.fromString(anId.value()))
                .map(TicketEntity::toTicket);
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        return save(ticket);
    }

    @Override
    @Transactional
    public Ticket update(Ticket ticket) {
        return save(ticket);
    }

    @Override
    public void deleteAll() {
        this.ticketJpaRepository.deleteAll();
    }

    private Ticket save(Ticket ticket) {
        this.outboxJpaRepository.saveAll(
                ticket.getAllDomainEvents().stream()
                        .map(it -> OutboxEntity.of(it, this::toJson))
                        .toList()
        );

        return this.ticketJpaRepository.save(TicketEntity.of(ticket))
                .toTicket();
    }

    private String toJson(final DomainEvent domainEvent) {
        try {
            return this.mapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException error) {
            throw new RuntimeException(error);
        }
    }
}
