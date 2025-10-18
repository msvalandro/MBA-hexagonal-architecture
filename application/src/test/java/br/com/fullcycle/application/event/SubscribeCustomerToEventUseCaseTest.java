package br.com.fullcycle.application.event;

import br.com.fullcycle.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.application.repository.InMemoryEventRepository;
import br.com.fullcycle.application.repository.InMemoryTicketRepository;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.ticket.TicketStatus;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubscribeCustomerToEventUseCaseTest {

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        // given
        final var expectedTicketsSize = 1;

        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");

        final var customerId = aCustomer.getCustomerId().value();
        final var eventId = anEvent.getEventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        customerRepository.create(aCustomer);
        eventRepository.create(anEvent);

        // when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var output = useCase.execute(subscribeInput);

        // then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.reservationDate());

        final var actualEvent = eventRepository.eventOfId(anEvent.getEventId());
        Assertions.assertEquals(expectedTicketsSize, actualEvent.get().getAllTickets().size());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        // given
        final var expectedError = "Event not found";

        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");

        final var customerId = aCustomer.getCustomerId().value();
        final var eventId = EventId.unique().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        customerRepository.create(aCustomer);

        // when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        // given
        final var expectedError = "Customer not found";

        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);

        final var customerId = CustomerId.unique().value();
        final var eventId = anEvent.getEventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        eventRepository.create(anEvent);

        // when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        // given
        final var expectedError = "Email already registered";

        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");

        final var customerId = aCustomer.getCustomerId().value();
        final var eventId = anEvent.getEventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        customerRepository.create(aCustomer);
        eventRepository.create(anEvent);

        final var ticket = anEvent.reserveTicket(aCustomer.getCustomerId());

        // when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar de um evento que está esgotado")
    public void testReserveTicketWithoutSlots() throws Exception {
        // given
        final var expectedError = "Event sold out";

        final var aPartner = Partner.newPartner("John Doe", "01.001.001/0001-11", "john.doe@mail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 1, aPartner);
        final var aCustomer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@mail.com");
        final var anotherCustomer = Customer.newCustomer("Jane Doe", "123.456.789-02", "jane.doe@mail.com");

        final var customerId = aCustomer.getCustomerId().value();
        final var eventId = anEvent.getEventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();

        customerRepository.create(aCustomer);
        customerRepository.create(anotherCustomer);
        eventRepository.create(anEvent);

        final var ticket = anEvent.reserveTicket(anotherCustomer.getCustomerId());

        // when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}