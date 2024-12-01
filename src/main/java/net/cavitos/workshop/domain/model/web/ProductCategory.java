package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.validator.ValueOfEnum;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductCategory {

    private String code;
    private String name;
    private String description;
    private int active;
}
