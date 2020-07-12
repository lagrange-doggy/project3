package com.mall.shopping.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 韩
 * @create 2020-07-12 2:35
 */
@Data
public class ShoppingGoodsVO implements Serializable {
    private Long productId;

    private BigDecimal salePrice;

    private String productName;

    private String subTitle;

    private String picUrl;
}
