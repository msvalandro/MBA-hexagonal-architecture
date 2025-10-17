package br.com.fullcycle.domain.person;

import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CnpjTest {

    @Test
    @DisplayName("Deve instanciar um CNPJ")
    public void testCreateCNPJ() {
        // given
        final var expectedCNPJ = "01.001.001/0001-01";

        // when
        final var actualCnpj = new Cnpj(expectedCNPJ);

        // then
        Assertions.assertEquals(expectedCNPJ, actualCnpj.value());
    }

    @Test
    @DisplayName("Não deve instanciar um CNPJ inválido")
    public void testCreateCNPJWithInvalidValue() {
        // given
        final var expectedError = "Invalid value for CNPJ";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cnpj("01.001001/0001-72")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um CNPJ nulo")
    public void testCreateCNPJWithNullValue() {
        // given
        final var expectedError = "Invalid value for CNPJ";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cnpj(null)
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
