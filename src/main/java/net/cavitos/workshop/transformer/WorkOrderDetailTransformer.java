package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class WorkOrderDetailTransformer implements Serializable {

    private final ZonedDateTimeFactory zonedDateTimeFactory;

    public WorkOrderDetailTransformer(final ZonedDateTimeFactory zonedDateTimeFactory) {

        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    public WorkOrderDetail toWeb(final WorkOrderDetailEntity entity) {

        final var workOrderEntity = entity.getWorkOrderEntity();
        final var productEntity = entity.getProductEntity();
        final var invoiceDetailEntity = entity.getInvoiceDetailEntity();

        final var detail = new WorkOrderDetail();
        detail.setId(entity.getId());
        detail.setOrderId(workOrderEntity.getId());
        detail.setQuantity(entity.getQuantity());
        detail.setUnitPrice(entity.getUnitPrice());
        detail.setSalePrice(entity.getSalePrice());
        detail.setDescription(entity.getDescription());
        detail.setNotes(entity.getNotes());

        if (nonNull(entity.getOperationDate())) {
            detail.setOperationDate(zonedDateTimeFactory.buildStringFromInstant(entity.getOperationDate()));
        }

        if (nonNull(invoiceDetailEntity)) {
            detail.setInvoiceDetailId(invoiceDetailEntity.getId());
        }

        if (Objects.nonNull(productEntity)) {
            final var product = new CommonProduct();
            product.setCode(productEntity.getCode());
            product.setStorable(productEntity.isStorable());
            product.setName(productEntity.getName());

            detail.setProduct(product);
        }

        return detail;
    }
}
