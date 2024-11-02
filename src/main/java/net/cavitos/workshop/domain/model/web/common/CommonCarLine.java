package net.cavitos.workshop.domain.model.web.common;

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
public class CommonCarLine {

    @NotEmpty
    @Size(max = 50)
    private String id;

    private String name;
}
