package br.com.fullcycle.hexagonal.application.domain.partner;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PartnerTest {

    @Test
    @DisplayName("Deve instanciar um parceiro")
    public void testCreatePartner() {
        // given
        final var expectedCNPJ = "01.001.001/0001-01";
        final var expectedEmail = "john.doe@mail.com";
        final var expectedName = "John Doe";

        // when
        final var actualPartner = Partner.newPartner(expectedName, expectedCNPJ, expectedEmail);

        // then
        Assertions.assertNotNull(actualPartner.getPartnerId());
        Assertions.assertEquals(expectedCNPJ, actualPartner.getCnpj().value());
        Assertions.assertEquals(expectedEmail, actualPartner.getEmail().value());
        Assertions.assertEquals(expectedName, actualPartner.getName().value());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com CNPJ inválido")
    public void testCreatePartnerWithInvalidCNPJ() {
        // given
        final var expectedError = "Invalid value for CNPJ";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner("John Doe", "01.001001/0001-01", "john.doe@mail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com nome inválido")
    public void testCreatePartnerWithInvalidName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner(null, "01.001.001/0001-01", "john.doe@mail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com email inválido")
    public void testCreatePartnerWithInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner("John Doe", "01.001.001/0001-01", "john.doemail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
