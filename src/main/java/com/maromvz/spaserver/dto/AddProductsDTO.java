package com.maromvz.spaserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddProductsDTO {
    private Long userId;
    private List<Long> productIds;
}
