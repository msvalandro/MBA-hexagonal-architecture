package br.com.fullcycle.domain.event.ticket;

import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.partner.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TicketTest {

    @Test
    @DisplayName("Deve instanciar um ticket")
    public void testCreateTicket() {
        // given
        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);

        final var expectedTicketStatus = TicketStatus.PENDING;
        final var expectedCustomerId = aCustomer.getCustomerId();
        final var expectedEventId = anEvent.getEventId();

        // when
        final var actualTicket = Ticket.newTicket(aCustomer.getCustomerId(), anEvent.getEventId());

        // then
        Assertions.assertNotNull(actualTicket.getTicketId());
        Assertions.assertNotNull(actualTicket.getReservedAt());
        Assertions.assertNull(actualTicket.getPaidAt());
        Assertions.assertEquals(expectedEventId, actualTicket.getEventId());
        Assertions.assertEquals(expectedCustomerId, actualTicket.getCustomerId());
        Assertions.assertEquals(expectedTicketStatus, actualTicket.getStatus());
    }
}
