package net.cavitos.workshop.model.entity.composite;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CodeTenantId {

    private String code;
    private String tenant;
}
