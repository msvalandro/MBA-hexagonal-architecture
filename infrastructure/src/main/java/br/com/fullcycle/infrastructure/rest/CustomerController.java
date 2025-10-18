package br.com.fullcycle.infrastructure.rest;

import br.com.fullcycle.application.Presenter;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.infrastructure.dtos.NewCustomerDTO;
import br.com.fullcycle.application.customer.CreateCustomerUseCase;
import br.com.fullcycle.application.customer.GetCustomerByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

// Adapter
@RestController
@RequestMapping(value = "customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> publicGetCustomerPresenter;
    private final Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> privateGetCustomerPresenter;


    public CustomerController(
            final CreateCustomerUseCase createCustomerUseCase,
            final GetCustomerByIdUseCase getCustomerByIdUseCase,
            final Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> privateGetCustomer,
            final Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> publicGetCustomer
    ) {
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
        this.getCustomerByIdUseCase = Objects.requireNonNull(getCustomerByIdUseCase);
        this.privateGetCustomerPresenter = privateGetCustomer;
        this.publicGetCustomerPresenter = publicGetCustomer;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewCustomerDTO dto) {
        try {
            final var output = createCustomerUseCase.execute(new CreateCustomerUseCase.Input(dto.cpf(), dto.email(), dto.name()));

            return ResponseEntity.created(URI.create("/customers/" + output.id())).body(output);
        } catch (ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable String id, @RequestHeader(name = "X-Public", required = false) String xPublic) {
        Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> presenter = privateGetCustomerPresenter;

        if (xPublic != null) {
            presenter = publicGetCustomerPresenter;
        }

        return getCustomerByIdUseCase.execute(new GetCustomerByIdUseCase.Input(id), presenter);
    }
}