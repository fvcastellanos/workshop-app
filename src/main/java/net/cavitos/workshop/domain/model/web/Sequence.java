package net.cavitos.workshop.domain.model.web;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class Sequence {

    private String id;
    private String prefix;
    private int padSize;
    private int stepSize;
    private String value;
    private String description;
}
