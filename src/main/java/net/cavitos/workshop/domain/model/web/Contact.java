package net.cavitos.workshop.domain.model.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Contact {

    @Size(max = 50)
    private String code;

    @NotEmpty
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
