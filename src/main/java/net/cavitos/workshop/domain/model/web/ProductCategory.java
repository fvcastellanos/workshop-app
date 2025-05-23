package net.cavitos.workshop.domain.model.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonSequence;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductCategory {

    private String code;
    private String name;
    private String description;
    private int active;
    private CommonSequence sequence;
}
