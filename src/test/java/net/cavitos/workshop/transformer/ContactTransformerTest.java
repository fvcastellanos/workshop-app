package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.model.entity.ContactEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContactTransformerTest {

    @Test
    void toWeb_shouldMapAllFieldsCorrectly() {
        // Arrange
        final var entity = ContactEntity.builder()
                .id("contact-1")
                .code("C001")
                .type("C")
                .name("John Doe")
                .description("A regular customer")
                .contact("john@example.com")
                .taxId("CF-123456")
                .active(1)
                .tenant("tenant-1")
                .build();

        // Act
        final var result = ContactTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result)
            .hasFieldOrPropertyWithValue("code", "C001")
            .hasFieldOrPropertyWithValue("type", "C")
            .hasFieldOrPropertyWithValue("name", "John Doe")
            .hasFieldOrPropertyWithValue("description", "A regular customer")
            .hasFieldOrPropertyWithValue("contact", "john@example.com")
            .hasFieldOrPropertyWithValue("taxId", "CF-123456")
            .hasFieldOrPropertyWithValue("active", 1);
    }

    @Test
    void toWeb_shouldHandleNullOptionalFields() {
        // Arrange
        ContactEntity entity = ContactEntity.builder()
                .id("contact-2")
                .code("C002")
                .type("P")
                .name("Jane Doe")
                .description(null)
                .contact(null)
                .taxId(null)
                .active(0)
                .tenant("tenant-2")
                .build();

        // Act
        Contact result = ContactTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result.getCode()).isEqualTo("C002");
        Assertions.assertThat(result.getType()).isEqualTo("P");
        Assertions.assertThat(result.getName()).isEqualTo("Jane Doe");
        Assertions.assertThat(result.getDescription()).isNull();
        Assertions.assertThat(result.getContact()).isNull();
        Assertions.assertThat(result.getTaxId()).isNull();
        Assertions.assertThat(result.getActive()).isEqualTo(0);
    }
}
