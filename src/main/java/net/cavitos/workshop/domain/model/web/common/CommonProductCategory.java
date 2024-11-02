package net.cavitos.workshop.domain.model.web.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CommonProductCategory {

    @NotBlank
    @Size(max = 50)
    private String id;

    @Size(max = 150)
    private String name;
}
