package br.com.fullcycle.hexagonal.application.domain.person;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    @DisplayName("Deve instanciar um Email")
    public void testCreateEmail() {
        // given
        final var expectedEmail = "john.doe@mail.com";

        // when
        final var actualEmail = new Email(expectedEmail);

        // then
        Assertions.assertEquals(expectedEmail, actualEmail.value());
    }

    @Test
    @DisplayName("Não deve instanciar um Email inválido")
    public void testCreateEmailWithInvalidValue() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email("john.doemail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um Email nulo")
    public void testCreateEmailWithNullValue() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email(null)
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
