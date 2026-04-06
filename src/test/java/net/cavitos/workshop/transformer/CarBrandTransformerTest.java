package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CarBrandTransformerTest {

    @Test
    void toWeb_shouldMapAllFieldsCorrectly() {
        // Arrange
        CarBrandEntity entity = CarBrandEntity.builder()
                .id("brand-1")
                .name("TOYOTA")
                .description("Japanese brand")
                .active(1)
                .tenant("tenant-1")
                .build();

        // Act
        CarBrand result = CarBrandTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result.getName()).isEqualTo("TOYOTA");
        Assertions.assertThat(result.getDescription()).isEqualTo("Japanese brand");
        Assertions.assertThat(result.getActive()).isEqualTo(1);
    }

    @Test
    void toWeb_shouldHandleNullDescription() {
        // Arrange
        CarBrandEntity entity = CarBrandEntity.builder()
                .id("brand-2")
                .name("HONDA")
                .description(null)
                .active(1)
                .tenant("tenant-2")
                .build();

        // Act
        CarBrand result = CarBrandTransformer.toWeb(entity);

        // Assert
        Assertions.assertThat(result.getName()).isEqualTo("HONDA");
        Assertions.assertThat(result.getDescription()).isNull();
        Assertions.assertThat(result.getActive()).isEqualTo(1);
    }
}

