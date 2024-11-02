package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.web.CarLine;
import net.cavitos.workshop.model.entity.CarLineEntity;

public final class CarLineTransformer {

    private CarLineTransformer() {
    }

    public static CarLine toWeb(final CarLineEntity carLineEntity) {

        final var active = ActiveStatus.of(carLineEntity.getActive())
                .name();

        final var carLine = new CarLine();
        carLine.setName(carLineEntity.getName());
        carLine.setDescription(carLineEntity.getDescription());
        carLine.setActive(active);

        return carLine;
    }
}
