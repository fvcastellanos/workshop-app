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
public class CommonOperationType {

    @NotBlank
    @Size(max = 150)
    private String code;

    @Size(max = 150)
    private String name;
}
