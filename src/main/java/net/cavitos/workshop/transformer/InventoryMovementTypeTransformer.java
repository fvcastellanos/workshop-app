package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.InventoryMovementType;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;

public final class InventoryMovementTypeTransformer {

    private InventoryMovementTypeTransformer() {
    }

    public static InventoryMovementType toWeb(final InventoryMovementTypeEntity entity) {

        final var view = new InventoryMovementType();
        view.setCode(entity.getCode());
        view.setName(entity.getName());
        view.setDescription(entity.getDescription());
        view.setType(entity.getType());
        view.setActive(entity.getActive());

        return view;
    }
}
