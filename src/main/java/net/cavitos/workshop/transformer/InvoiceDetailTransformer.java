package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.type.ProductType;
import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;

import java.util.Objects;

public final class InvoiceDetailTransformer {

    private InvoiceDetailTransformer() {
    }

    public static InvoiceDetail toWeb(InvoiceDetailEntity entity) {

        final var workOrderEntity = entity.getWorkOrderEntity();
        final var productEntity = entity.getProductEntity();

        final var productType = ProductType.of(productEntity.getType())
                .name();

        final var product = new CommonProduct();
        product.setCode(productEntity.getCode());
        product.setName(productEntity.getName());
        product.setType(productType);

        final var detail = new InvoiceDetail();
        detail.setId(entity.getId());
        detail.setQuantity(entity.getQuantity());
        detail.setUnitPrice(entity.getUnitPrice());
        detail.setDiscountAmount(entity.getDiscountAmount());
        detail.setTotal(entity.getQuantity() * entity.getUnitPrice());
        detail.setProduct(product);

        if (Objects.nonNull(workOrderEntity)) {

            detail.setWorkOrderNumber(workOrderEntity.getNumber());
        }

        return detail;
    }
}
