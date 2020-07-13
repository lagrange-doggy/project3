package com.mall.order.services;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderDetailsVO;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * ciggar
 * create-date: 2019/7/30-上午10:05
 */
@Slf4j
@Component
@Service(cluster = "failfast")
public class OrderCoreServiceImpl implements OrderCoreService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;


    /**
     * 创建订单的处理流程
     *
     * @param request
     * @return
     */
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        CreateOrderResponse response = new CreateOrderResponse();
        try {
            //创建pipeline对象
            TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

            //启动pipeline
            invoker.start(); //启动流程（pipeline来处理）

            //获取处理结果
            AbsTransHandlerContext context = invoker.getContext();

            //把处理结果转换为response
            response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);
        } catch (Exception e) {
            log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderDetailsVO selectorderDetailsByOrderIdAndUserId(Integer orderId, Long userId) {
        //查询同时符合orderid和userid的订单信息
        OrderDetailsVO orderDetailsVO = orderMapper.selectorderDetailsByOrderId(orderId, userId);
        //不符合 会查不到 传null
        if (orderDetailsVO.getUserId() == null || orderDetailsVO.getUserName() == null) {
            throw new BizException("**********数据注入异常,userid与订单号不一致**********");
        }
        orderDetailsVO.setGoodsList(orderMapper.selectOrderGoodsListByOrderIdAndUserId(orderId));
        return orderDetailsVO;
    }


}
