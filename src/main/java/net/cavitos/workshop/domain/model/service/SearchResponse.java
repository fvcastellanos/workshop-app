package net.cavitos.workshop.domain.model.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class SearchResponse<T> {

    private Page<T> entities;
    private long totalCount;
}
