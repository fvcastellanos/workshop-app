package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;

import static java.util.Objects.nonNull;

public class WorkOrderDetailTransformer {

    private WorkOrderDetailTransformer() {
    }

    public static WorkOrderDetail toWeb(final WorkOrderDetailEntity entity) {

        final var workOrderEntity = entity.getWorkOrderEntity();
        final var productEntity = entity.getProductEntity();
        final var invoiceDetailEntity = entity.getInvoiceDetailEntity();

        final var product = new CommonProduct();
        product.setCode(productEntity.getCode());
        product.setStorable(productEntity.isStorable());
        product.setName(productEntity.getName());

        final var detail = new WorkOrderDetail();
        detail.setId(entity.getId());
        detail.setOrderId(workOrderEntity.getId());
        detail.setQuantity(entity.getQuantity());
        detail.setUnitPrice(entity.getUnitPrice());
        detail.setProduct(product);

        if (nonNull(invoiceDetailEntity)) {

            detail.setInvoiceDetailId(invoiceDetailEntity.getId());
        }

        return detail;
    }
}
