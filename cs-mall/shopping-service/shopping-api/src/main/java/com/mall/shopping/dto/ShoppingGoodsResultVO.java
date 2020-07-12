package com.mall.shopping.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 韩
 * @create 2020-07-12 1:31
 */
@Data
public class ShoppingGoodsResultVO {

    private List<ShoppingGoodsVO> data;

    private Integer total;
}
