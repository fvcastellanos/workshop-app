package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.type.ContactType;
import net.cavitos.workshop.domain.model.validator.ValueOfEnum;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Contact {

    @Size(max = 50)
    private String code;

    @NotEmpty
    @ValueOfEnum(enumType = ContactType.class, message = "Invalid type, allowed values: CUSTOMER|PROVIDER")
    private String type;

    @NotEmpty
    @Size(max = 150)
    private String name;

    @Size(max = 300)
    private String description;

    @Size(max = 150)
    private String contact;

    @Size(max = 50)
    private String taxId;

    private int active;
}
