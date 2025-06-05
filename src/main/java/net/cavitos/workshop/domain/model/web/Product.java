package net.cavitos.workshop.domain.model.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonProductCategory;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Product {

    private String code;
    private boolean storable;
    private String name;
    private String description;
    private double minimalQuantity;
    private int active;
    private CommonProductCategory category;
}
