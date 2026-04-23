package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.CarLine;
import net.cavitos.workshop.model.entity.CarLineEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CarLineTransformerTest {

    @Test
    void toWeb_shouldMapAllFieldsCorrectly() {
        // Arrange
        CarLineEntity entity = CarLineEntity.builder()
                .id("line-1")
                .name("Corolla")
                .description("Sedan line")
                .active(1)
                .tenant("tenant-1")
                .build();

        // Act
        CarLine result = CarLineTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result.getName()).isEqualTo("Corolla");
        Assertions.assertThat(result.getDescription()).isEqualTo("Sedan line");
        Assertions.assertThat(result.getActive()).isEqualTo(1);
    }

    @Test
    void toWeb_shouldHandleNullDescription() {
        // Arrange
        CarLineEntity entity = CarLineEntity.builder()
                .id("line-2")
                .name("Yaris")
                .description(null)
                .active(0)
                .tenant("tenant-2")
                .build();

        // Act
        CarLine result = CarLineTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result.getName()).isEqualTo("Yaris");
        Assertions.assertThat(result.getDescription()).isNull();
        Assertions.assertThat(result.getActive()).isEqualTo(0);
    }
}
