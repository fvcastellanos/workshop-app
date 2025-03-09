package net.cavitos.workshop.views.inventory;

import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.service.InventoryMovementService;
import net.cavitos.workshop.views.component.DeleteDialog;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitialInventoryDeleteDialog extends DeleteDialog<InventoryEntity> {

    final InventoryMovementService inventoryMovementService;
    final ZonedDateTimeFactory zonedDateTimeFactory;

    public InitialInventoryDeleteDialog(final InventoryMovementService inventoryMovementService,
                                        final ZonedDateTimeFactory zonedDateTimeFactory) {
        super();

        this.inventoryMovementService = inventoryMovementService;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    @Override
    protected String getEntityName() {

        final var inventoryEntity = getEntity();

        final var operationDate = zonedDateTimeFactory.buildStringFromInstant(inventoryEntity.getOperationDate());

        final var detail = "%s - %s - %s - %s - %s".formatted(
                operationDate,
                inventoryEntity.getQuantity(),
                inventoryEntity.getProductEntity().getCode(),
                inventoryEntity.getProductEntity().getName(),
                inventoryEntity.getUnitPrice()
        );

        return "¿Esta seguro de eliminar el detalle [%s]?".formatted(detail);
    }

    @Override
    protected void deleteEntity(InventoryEntity entity) {

        inventoryMovementService.delete(entity.getId(), getTenant());
    }
}
