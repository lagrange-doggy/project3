package com.mall.order.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.order.constant.GoodsListVO;
import com.mall.order.constant.OrderDetailsVO;
import com.mall.order.dal.entitys.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends TkMapper<Order> {
    Long countAll();

    OrderDetailsVO selectorderDetailsByOrderId(@Param("id") Integer id, @Param("uid") Long uid);

    List<GoodsListVO> selectOrderGoodsListByOrderIdAndUserId(@Param("id") Integer orderId);
}
