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

    @Size(max = 50)
    private String code;

    @NotEmpty
    @ValueOfEnum(enumType = ProductType.class, message = "Invalid type, allowed values: PRODUCT|SERVICE")
    private String type;

    @NotEmpty
    @Size(max = 150)
    private String name;

    @Size(max = 300)
    private String description;

    @NotNull
    private double minimalQuantity;

    @ValueOfEnum(enumType = ActiveStatus.class, message = "Invalid type, allowed values: ACTIVE|INACTIVE")
    private String active;

    @NotNull
    private CommonProductCategory category;
}
