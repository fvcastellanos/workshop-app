package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WorkOrderDetail {

    @Size(max = 50)
    private String id;

    @NotBlank
    @Size(max = 50)
    private String orderId;

    @Size(max = 50)
    private String invoiceDetailId;

    @NotNull
    private CommonProduct product;

    @Min(0)
    @NotNull
    private double quantity;

    @Min(0)
    @NotNull
    private double unitPrice;
}
