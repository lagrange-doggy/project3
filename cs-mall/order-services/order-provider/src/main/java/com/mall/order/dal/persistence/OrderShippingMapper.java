package com.mall.order.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.order.dal.entitys.OrderShipping;
import org.apache.ibatis.annotations.Param;

public interface OrderShippingMapper extends TkMapper<OrderShipping> {

    //Fang
    OrderShipping queryByOrderId(@Param("orderId") String orderId);
}