package net.cavitos.workshop.views.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Search {

    private String text;
    private String code;

    private int active;
    private int page;
    private int size;
}
