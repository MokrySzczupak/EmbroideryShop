package com.example.embroideryshop.service;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public class SortCriteria {
    private final ProductProperty property;
    private final Sort.Direction direction;
    private static final int PROPERTY_INDEX = 1;
    private static final int DIRECTION_INDEX = 0;

    public SortCriteria(Sort.Direction direction, ProductProperty property) {
        this.direction = direction;
        this.property = property;
    }

    /***
     *
     * @param sortQuery template: property-sortDirection
     * @return new SortCriteria from given query
     */
    public static SortCriteria fromQuery(String sortQuery) {
        Sort.Direction directionTemp = resolveDirectionFromQuery(sortQuery);
        ProductProperty propertyTemp = resolvePropertyFromQuery(sortQuery);
        return new SortCriteria(directionTemp, propertyTemp);
    }

    private static Sort.Direction resolveDirectionFromQuery(String sortQuery) {
        return Sort.Direction.fromString(sortQuery.split("-")[DIRECTION_INDEX]);
    }

    private static ProductProperty resolvePropertyFromQuery(String sortQuery) {
        return ProductProperty.fromString(sortQuery.split("-")[PROPERTY_INDEX]);
    }

}
