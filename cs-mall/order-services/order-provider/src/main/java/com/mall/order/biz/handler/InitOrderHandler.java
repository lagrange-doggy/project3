package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.commons.tool.utils.NumberUtils;
import com.mall.order.biz.callback.SendEmailCallback;
import com.mall.order.biz.callback.TransCallback;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dto.CartProductDto;
import com.mall.order.utils.GlobalIdGeneratorUtil;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ciggar
 * create-date: 2019/8/1-下午5:01
 * 初始化订单 生成订单
 */

@Slf4j
@Component
public class InitOrderHandler extends AbstractTransHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        //插入订单表
        Order order = new Order();
        String orderId = UUID.randomUUID().toString();
        //quanjuid生成器（发号器）
        order.setOrderId(orderId);
        order.setUserId(createOrderContext.getUserId());
        order.setBuyerNick(createOrderContext.getBuyerNickName());
        order.setPayment(createOrderContext.getOrderTotal());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setStatus(OrderConstants.ORDER_STATUS_INIT);
        orderMapper.insert(order);

        //插入订单商品关联表

        List<Long> buyProductIds = new ArrayList<>();

        List<CartProductDto> cartProductDtos = createOrderContext.getCartProductDtoList();
        for (CartProductDto cartProductDto : cartProductDtos) {
            OrderItem orderItem = new OrderItem();
            String orderItmenId = UUID.randomUUID().toString();
            orderItem.setId(orderItmenId);
            orderItem.setItemId(cartProductDto.getProductId());
            orderItem.setOrderId(orderId);
            orderItem.setNum(cartProductDto.getProductNum().intValue());
            orderItem.setPrice(cartProductDto.getSalePrice().doubleValue());
            orderItem.setTitle(cartProductDto.getProductName());
            orderItem.setPicPath(cartProductDto.getProductImg());
            BigDecimal total = cartProductDto.getSalePrice().multiply(new BigDecimal(
                    cartProductDto.getProductNum()));

            orderItem.setTotalFee(total.doubleValue());
            //已锁定库存
            orderItem.setStatus(1);
            buyProductIds.add(cartProductDto.getProductId());

            int insert = orderItemMapper.insert(orderItem);
            if (insert == 0) {
                throw new BizException("订单商品关联表数据插入失败");
            }
        }
        createOrderContext.setOrderId(orderId);
        createOrderContext.setBuyProductIds(buyProductIds);

        return true;
    }
}
