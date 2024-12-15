package net.cavitos.workshop.domain.model.web.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CommonProduct {

    private String code;
    private String name;
    private String type;
}
