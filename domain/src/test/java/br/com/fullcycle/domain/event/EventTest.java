package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.event.ticket.TicketStatus;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

public class EventTest {

    @Test
    @DisplayName("Deve instanciar um evento")
    public void testCreateEvent() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedTotalPartnerId = aPartner.getPartnerId().value();
        final var expectedTickets = 0;

        // when
        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);

        // then
        Assertions.assertNotNull(actualEvent.getEventId());
        Assertions.assertEquals(expectedDate, actualEvent.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(expectedName, actualEvent.getName().value());
        Assertions.assertEquals(expectedTotalSpots, actualEvent.getTotalSpots());
        Assertions.assertEquals(expectedTotalPartnerId, actualEvent.getPartnerId().value());
        Assertions.assertEquals(expectedTickets, actualEvent.getAllTickets().size());
    }

    @Test
    @DisplayName("Não deve instanciar um evento com nome inválido")
    public void testCreateEventWithInvalidName() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");

        final var expectedError = "Invalid value for Name";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Event.newEvent(null, "2021-01-01", 10, aPartner)
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um evento com data inválida")
    public void testCreateEventWithInvalidDate() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");

        final var expectedError = "Invalid date for Event";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Event.newEvent("Disney on Ice", "2021-01-01A", 10, aPartner)
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Deve reservar um ticket quando é possível")
    public void testReserveTicket() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");

        final var expectedCustomerId = aCustomer.getCustomerId();
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedTotalPartnerId = aPartner.getPartnerId().value();
        final var expectedTickets = 1;
        final var expectedTicketOrder = 1;
        final var expectedTicketStatus = TicketStatus.PENDING;

        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);

        final var expectedEventId = actualEvent.getEventId();

        // when
        final var actualTicket = actualEvent.reserveTicket(aCustomer.getCustomerId());

        // then
        Assertions.assertNotNull(actualTicket.getTicketId());
        Assertions.assertNotNull(actualTicket.getReservedAt());
        Assertions.assertNull(actualTicket.getPaidAt());
        Assertions.assertEquals(expectedEventId, actualTicket.getEventId());
        Assertions.assertEquals(expectedCustomerId, actualTicket.getCustomerId());
        Assertions.assertEquals(expectedTicketStatus, actualTicket.getStatus());

        Assertions.assertNotNull(expectedEventId);
        Assertions.assertEquals(expectedDate, actualEvent.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(expectedName, actualEvent.getName().value());
        Assertions.assertEquals(expectedTotalSpots, actualEvent.getTotalSpots());
        Assertions.assertEquals(expectedTotalPartnerId, actualEvent.getPartnerId().value());
        Assertions.assertEquals(expectedTickets, actualEvent.getAllTickets().size());

        final var actualEventTicket = actualEvent.getAllTickets().iterator().next();
        Assertions.assertEquals(expectedTicketOrder, actualEventTicket.getOrdering());
        Assertions.assertEquals(expectedEventId, actualEventTicket.getEventId());
        Assertions.assertEquals(expectedCustomerId, actualEventTicket.getCustomerId());
        Assertions.assertEquals(actualTicket.getTicketId(), actualEventTicket.getTicketId());
    }

    @Test
    @DisplayName("Não deve reservar um ticket quando o evento está esgotado")
    public void testReserveTicketWhenEventIsSoldOut() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");
        final var anotherCustomer = Customer.newCustomer("Jane Doe", "123.456.789-02", "jane.doe@mail.com");

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 1;

        final var expectedError = "Event sold out";

        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);

        actualEvent.reserveTicket(anotherCustomer.getCustomerId());


        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer.getCustomerId())
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve reservar dois tickets para um mesmo cliente")
    public void testReserveTicketForSameCustomer() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 1;

        final var expectedError = "Email already registered";

        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);

        actualEvent.reserveTicket(aCustomer.getCustomerId());


        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer.getCustomerId())
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
