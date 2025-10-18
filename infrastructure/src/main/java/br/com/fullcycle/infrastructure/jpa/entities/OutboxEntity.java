package br.com.fullcycle.infrastructure.jpa.entities;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Entity(name = "Outbox")
@Table(name = "outbox")
public class OutboxEntity {

    @Id
    private UUID id;

    @Column(columnDefinition = "JSON", length = 4_000)
    private String content;

    private boolean published;

    public OutboxEntity() {
    }

    public OutboxEntity(UUID id, String content, boolean published) {
        this.id = id;
        this.content = content;
        this.published = published;
    }

    public static OutboxEntity of(final DomainEvent domainEvent, final Function<DomainEvent, String> toJson) {
        return new OutboxEntity(
                UUID.fromString(domainEvent.domainEventId()),
                toJson.apply(domainEvent),
                false
        );
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OutboxEntity that = (OutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public OutboxEntity notePublished() {
        this.published = true;
        return this;
    }
}
