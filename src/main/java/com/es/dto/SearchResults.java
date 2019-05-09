package com.es.dto;

import java.util.List;
import java.util.Map;

public class SearchResults {

	private List<Map<String, Object>> list;
    private Long totalRecords;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
