package br.com.fullcycle.infrastructure.rest;

import br.com.fullcycle.application.partner.CreatePartnerUseCase;
import br.com.fullcycle.application.partner.GetPartnerByIdUseCase;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.infrastructure.dtos.NewPartnerDTO;
import br.com.fullcycle.infrastructure.http.HttpRouter;
import br.com.fullcycle.infrastructure.http.HttpRouter.HttpRequest;
import br.com.fullcycle.infrastructure.http.HttpRouter.HttpResponse;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

public class PartnerFnController {

    private final CreatePartnerUseCase createPartnerUseCase;
    private final GetPartnerByIdUseCase getPartnerByIdUseCase;

    public PartnerFnController(
            final CreatePartnerUseCase createPartnerUseCase,
            final GetPartnerByIdUseCase getPartnerByIdUseCase
    ) {
       this.createPartnerUseCase = Objects.requireNonNull(createPartnerUseCase);
       this.getPartnerByIdUseCase = Objects.requireNonNull(getPartnerByIdUseCase);
    }

    public HttpRouter bind(final HttpRouter router) {
        router.GET("/partners/{id}", this::get);
        router.POST("/partners", this::create);

        return router;
    }

    public HttpResponse<?> create(final HttpRequest request) {
        try {
            final var dto = request.body(NewPartnerDTO.class);

            final var output = createPartnerUseCase.execute(new CreatePartnerUseCase.Input(dto.cnpj(), dto.email(), dto.name()));

            return HttpResponse.created(URI.create("/partners/" + output.id())).body(output);
        } catch (ValidationException ex) {
            return HttpResponse.unprocessableEntity().body(ex.getMessage());
        }
    }

    public HttpResponse<?> get(final HttpRequest request) {
        final String id = request.pathParams("id");
        final var partner = getPartnerByIdUseCase.execute(new GetPartnerByIdUseCase.Input(id));

        if (partner.isEmpty()) {
            return HttpResponse.notFound().build();
        }

        return HttpResponse.ok(partner.get());
    }

}
