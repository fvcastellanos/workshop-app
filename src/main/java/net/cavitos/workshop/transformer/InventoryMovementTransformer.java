package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.domain.model.web.common.CommonOperationType;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;

import static java.util.Objects.nonNull;

public class InventoryMovementTransformer {

    public static InventoryMovement toWeb(final InventoryEntity entity, ZonedDateTimeFactory zonedDateTimeFactory) {

        final var product = entity.getProductEntity();
        final var invoiceDetail = entity.getInvoiceDetailEntity();
        final var operationType = entity.getInventoryMovementTypeEntity();

        final var commonProduct = new CommonProduct();
        commonProduct.setCode(product.getCode());
        commonProduct.setName(product.getName());
        commonProduct.setStorable(product.isStorable());

        final var commonOperationType = new CommonOperationType();
        commonOperationType.setCode(operationType.getCode());
        commonOperationType.setName(operationType.getName());

        final var movement = new InventoryMovement();
        movement.setId(entity.getId());
        movement.setQuantity(entity.getQuantity());
        movement.setUnitPrice(entity.getUnitPrice());
        movement.setDiscountAmount(entity.getDiscountAmount());
        movement.setOperationType(commonOperationType);

        if (nonNull(invoiceDetail)) {
            movement.setInvoiceDetailId(invoiceDetail.getId());
        }

        movement.setOperationDate(zonedDateTimeFactory.buildStringFromInstant(entity.getOperationDate()));
        movement.setDescription(entity.getDescription());
        movement.setProduct(commonProduct);

        return movement;
    }
}
