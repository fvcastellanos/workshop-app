package net.cavitos.workshop.domain.model.error;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FieldError {

    private String fieldName;
    private String value;
    private String error;    
}
