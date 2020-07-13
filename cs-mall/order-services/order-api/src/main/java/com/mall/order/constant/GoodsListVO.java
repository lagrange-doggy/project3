package com.mall.order.constant;

import lombok.Data;

/**
 * @author 韩
 * @create 2020-07-13 2:32
 */
@Data
public class GoodsListVO {

    /**
     * 订单关联表id
     */
    private String id;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 该商品购买数目
     */
    private Integer num;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 商品总金额
     */
    private Double totalFee;

    /**
     * 商品图片地址
     */
    private String picPath;
}
