package com.mall.order.constant;

import com.mall.order.dto.OrderDetailInfo;
import lombok.Data;

import java.util.List;

/**
 * @author Fang
 * 返回当前用户的订单时需要
 */
@Data
public class OrderListByUserIdVO {
    private List<OrderDetailInfo> data;
    private Long total;
}
