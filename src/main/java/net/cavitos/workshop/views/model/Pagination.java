package net.cavitos.workshop.views.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Pagination {

        private int page;
        private int size;
        private int totalElements;
        private int totalPages;
}
