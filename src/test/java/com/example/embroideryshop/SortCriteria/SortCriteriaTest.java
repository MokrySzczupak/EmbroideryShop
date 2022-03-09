package com.example.embroideryshop.SortCriteria;


import com.example.embroideryshop.service.ProductProperty;
import com.example.embroideryshop.service.SortCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortCriteriaTest {

    @Test
    public void sortAscending() {
        SortCriteria sortCriteria = SortCriteria.fromQuery("asc-price");
        assertEquals(Sort.Direction.ASC, sortCriteria.getDirection());
    }

    @Test
    public void sortByName() {
        SortCriteria sortCriteria = SortCriteria.fromQuery("desc-name");
        assertEquals(ProductProperty.NAME, sortCriteria.getProperty());
    }

    @Test void sortByPriceDescending() {
        SortCriteria sortCriteria = SortCriteria.fromQuery("desc-price");
        assertEquals(Sort.by(sortCriteria.getDirection(), sortCriteria.getProperty().toString()),
                Sort.by(Sort.Direction.DESC, ProductProperty.PRICE.toString()));
    }
}
