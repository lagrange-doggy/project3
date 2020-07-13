package com.mall.order.constant;

import lombok.Data;

import java.util.List;

/**
 * @author 韩
 * @create 2020-07-13 2:27
 */
@Data
public class OrderDetailsVO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 订单总价
     */
    private Double orderTotal;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 订单商品列表
     */
    private List<GoodsListVO> goodsList;

    /**
     * 电话
     */
    private String tel;

    /**
     * 地址
     */
    private String streetName;

    /**
     * 订单状态
     */
    private Integer orderStatus;

}
