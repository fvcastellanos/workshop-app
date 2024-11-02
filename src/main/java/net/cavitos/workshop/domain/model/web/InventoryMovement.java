package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.validator.Date;
import net.cavitos.workshop.domain.model.web.common.CommonOperationType;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InventoryMovement {

    @Size(max = 50)
    private String id;

    @NotNull
    private CommonProduct product;

    @NotNull
    private CommonOperationType operationType;

    @Date
    @NotEmpty
    private String operationDate;

    @Size(max = 50)
    private String invoiceDetailId;

    @Min(0)
    @NotNull
    private double quantity;

    @Min(0)
    @NotNull
    private double unitPrice;

    @Min(0)
    private double discountAmount;

    @Size(max = 200)
    private String description;
}
