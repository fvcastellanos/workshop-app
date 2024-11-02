package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.model.entity.CarBrandEntity;

public final class CarBrandTransformer {

    private CarBrandTransformer() {
    }

    public static CarBrand toWeb(final CarBrandEntity carBrandEntity) {

        final var carBrand = new CarBrand();
        carBrand.setName(carBrandEntity.getName());
        carBrand.setDescription(carBrandEntity.getDescription());
        carBrand.setActive(carBrandEntity.getActive());

        return carBrand;
    }
}
