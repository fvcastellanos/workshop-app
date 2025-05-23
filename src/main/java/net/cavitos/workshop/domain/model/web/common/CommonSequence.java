package net.cavitos.workshop.domain.model.web.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class CommonSequence {

    private String id;
    private String prefix;
}
