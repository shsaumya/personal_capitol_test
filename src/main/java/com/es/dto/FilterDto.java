package com.es.dto;

import com.es.filter.StringOperator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class FilterDto {
    private String filterName;
    private StringOperator operator;
    private Set<String> values = new HashSet<>();

    public FilterDto() {
    }

    public FilterDto(String filterName, StringOperator operator, String value) {

        this.filterName = filterName;
        this.operator = operator;
        this.values.add(value);
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public StringOperator getOperator() {
        return operator;
    }

    public void setOperator(StringOperator operator) {
        this.operator = operator;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        values.add(value);
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }
}
