package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.type.InventoryOperationType;
import net.cavitos.workshop.domain.model.web.InventoryMovementType;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;

public final class InventoryMovementTypeTransformer {

    private InventoryMovementTypeTransformer() {
    }

    public static InventoryMovementType toWeb(final InventoryMovementTypeEntity entity) {

        final var active = ActiveStatus.of(entity.getActive())
                .name();

        final var type = InventoryOperationType.of(entity.getType())
                .name();

        final var view = new InventoryMovementType();
        view.setCode(entity.getCode());
        view.setName(entity.getName());
        view.setDescription(entity.getDescription());
        view.setType(type);
        view.setActive(active);

        return view;
    }
}
