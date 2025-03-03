package net.cavitos.workshop.views.provider;

import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.service.InvoiceDetailService;
import net.cavitos.workshop.views.component.DeleteDialog;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProviderInvoiceDeleteDialog extends DeleteDialog<InvoiceDetail> {

    private final InvoiceDetailService invoiceDetailService;

    public ProviderInvoiceDeleteDialog(final InvoiceDetailService invoiceDetailService) {
        super();
        this.invoiceDetailService = invoiceDetailService;
    }

    @Override
    protected String getEntityName() {

        final var detailEntity = getEntity();

        final var detail = "%s - %s - %s - %s".formatted(
                detailEntity.getQuantity(),
                detailEntity.getProduct().getCode(),
                detailEntity.getProduct().getName(),
                detailEntity.getUnitPrice()
        );

        return "¿Esta seguro de eliminar el detalle [%s]?".formatted(detail);
    }

    @Override
    protected void deleteEntity(final InvoiceDetail entity) {

        invoiceDetailService.delete(getTenant(), entity.getInvoiceId(), entity.getId());
    }
}
