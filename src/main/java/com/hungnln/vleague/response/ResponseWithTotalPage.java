package com.hungnln.vleague.response;

import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ResponseWithTotalPage<T> {
    private int totalPage;
    private int totalCount;
    private int pageSize;
    private int pageIndex;
    private List<T> responseList;
}
