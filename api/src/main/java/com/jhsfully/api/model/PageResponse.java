package com.jhsfully.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private long totalElements;
    private boolean hasNextPage;
    @Builder.Default
    private List<T> content = new ArrayList<>();

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .totalElements(page.getTotalElements())
            .hasNextPage(page.hasNext())
            .content(page.getContent())
            .build();
    }

    public static <T, R> PageResponse<R> of(Page<T> page, Function<? super T, ? extends R> mapper) {
        return PageResponse.<R>builder()
            .totalElements(page.getTotalElements())
            .hasNextPage(page.hasNext())
            .content(
                page.getContent()
                    .stream()
                    .map(mapper)
                    .collect(Collectors.toList())
            )
            .build();
    }
}
