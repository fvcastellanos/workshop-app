package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.notification.NotificationVariant;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.views.component.DeleteDialog;
import net.cavitos.workshop.views.factory.NotificationFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderDetailDeleteDialog extends DeleteDialog<WorkOrderDetailEntity> {

    private static final String WORK_ORDER_DETAIL_WITH_PRODUCT = "%s - %s, Cantidad: %.2f, Precio Venta: %.2f";
    private static final String WORK_ORDER_DETAIL_WITH_LABOR = "%s, Precio: %.2f";

    private final WorkOrderDetailService workOrderDetailService;

    public OrderDetailDeleteDialog(final WorkOrderDetailService workOrderDetailService) {
        super();
        this.workOrderDetailService = workOrderDetailService;
    }

    @Override
    protected String getEntityName() {

        return buildEntityName(getEntity());
    }

    @Override
    protected void deleteEntity(WorkOrderDetailEntity entity) {

        try {
            final var workOrder = entity.getWorkOrderEntity();
            final var tenant = getTenant();

            workOrderDetailService.deleteOrderDetail(workOrder.getId(), entity.getId(), tenant);
        } catch (Exception exception) {

            NotificationFactory.showPersistentNotification(exception.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private String buildEntityName(final WorkOrderDetailEntity entity) {

        if (Objects.isNull(entity.getProductEntity())) {

            return WORK_ORDER_DETAIL_WITH_LABOR.formatted(
                    entity.getDescription(),
                    entity.getSalePrice()
            );
        }

        return WORK_ORDER_DETAIL_WITH_PRODUCT.formatted(
                entity.getProductEntity().getCode(),
                entity.getProductEntity().getName(),
                entity.getQuantity(),
                entity.getSalePrice()
        );
    }
}
