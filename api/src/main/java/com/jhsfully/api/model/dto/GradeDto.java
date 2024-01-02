package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GradeDto {

    private long id;
    private String gradeName;
    private long price;
    private int apiMaxCount;
    private int fieldMaxCount;
    private int queryMaxCount;
    private int recordMaxCount;
    private int dbMaxSize;
    private int accessorMaxCount;
    private int historyStorageDays;

    public static GradeDto of(Grade entity) {
        return GradeDto.builder()
            .id(entity.getId())
            .gradeName(entity.getGradeName())
            .price(entity.getPrice())
            .apiMaxCount(entity.getApiMaxCount())
            .fieldMaxCount(entity.getFieldMaxCount())
            .queryMaxCount(entity.getQueryMaxCount())
            .recordMaxCount(entity.getRecordMaxCount())
            .dbMaxSize(entity.getDbMaxSize())
            .accessorMaxCount(entity.getAccessorMaxCount())
            .historyStorageDays(entity.getHistoryStorageDays())
            .build();
    }

}
