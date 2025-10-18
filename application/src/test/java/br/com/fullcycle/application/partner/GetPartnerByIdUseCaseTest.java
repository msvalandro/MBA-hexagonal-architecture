package br.com.fullcycle.application.partner;

import br.com.fullcycle.application.repository.InMemoryPartnerRepository;
import br.com.fullcycle.domain.partner.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class GetPartnerByIdUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testGetById() {
        // given
        final var expectedCNPJ = "00.001.001/0001-01";
        final var expectedEmail = "john.doe@mail.com";
        final var expectedName = "John Doe";

        final var aPartner = Partner.newPartner(expectedName, expectedCNPJ, expectedEmail);

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var expectedId = aPartner.getPartnerId().value().toString();
        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var useCase = new GetPartnerByIdUseCase(partnerRepository);
        final var output = useCase.execute(input).get();

        // then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um parceiro não existente por id")
    public void testGetByIdWithInvalidId() {
        // given
        final var expectedId = UUID.randomUUID().toString();

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var partnerRepository = new InMemoryPartnerRepository();
        final var useCase = new GetPartnerByIdUseCase(partnerRepository);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }
}