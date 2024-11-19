package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.type.ProductType;
import net.cavitos.workshop.domain.model.validator.ValueOfEnum;
import net.cavitos.workshop.domain.model.web.common.CommonProductCategory;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Product {

    private String code;
    private String type;
    private String name;
    private String description;
    private double minimalQuantity;
    private int active;
    private CommonProductCategory category;
}
