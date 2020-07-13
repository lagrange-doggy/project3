package com.mall.order.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dal.entitys.*;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:04
 */
@Slf4j
@Component
@Service
public class OrderQueryServiceImpl implements OrderQueryService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Autowired
    OrderConverter orderConverter;

    /**
     *  Fang
     *  用户订单信息
     */
    @Override
    public OrderListResponse orderList(OrderListRequest request) {
        OrderListResponse response = new OrderListResponse();
        try {
            request.requestCheck();
            //获取request中的数据
            Integer page = request.getPage();
            Integer size = request.getSize();
            //分页
            PageHelper.startPage(page, size);
            long total = 0;
            //sort为空，没接收，但是sql语句中按照updateTime降序排序
            //String sort = request.getSort();

            Long userId = request.getUserId();
            //根据userId去获得对应的订单信息
            List<Order> orders = orderMapper.selectOrderByUserId(userId);
            if(orders == null){
                //
                response.setCode(OrderRetCode.SUCCESS.getCode());
                response.setMsg(OrderRetCode.SUCCESS.getMessage());
                return response;
            }
            //先获取order表信息，并转换成OrderDetailInfo
            List<OrderDetailInfo> orderDetailInfos = orderConverter.orderList2detailList(orders);
            //然后获取orderItem和orderShopping信息，并转换成对应的Dto，放进OrderDetailInfo中
            for (OrderDetailInfo orderDetailInfo : orderDetailInfos) {
                String orderId = orderDetailInfo.getOrderId();
                //先获取OrderItem
                List<OrderItem> orderItems = orderItemMapper.queryByOrderId(orderId);
                //转换成对应的OrderItemDto
                List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
                //再获取orderShipping
                OrderShipping orderShipping = orderShippingMapper.queryByOrderId(orderId);
                //转换成对应的OrderShippingDto
                OrderShippingDto orderShippingDto = orderConverter.shipping2dto(orderShipping);

                //全部放入orderDetailInfo中
                orderDetailInfo.setOrderItemDto(orderItemDtos);
                orderDetailInfo.setOrderShippingDto(orderShippingDto);
            }
            //分页
            PageInfo<OrderDetailInfo> orderDetailInfoPageInfo = new PageInfo<>(orderDetailInfos);
            total = orderDetailInfoPageInfo.getTotal();

            //最后放入response中
            response.setDetailInfoList(orderDetailInfos);
            response.setTotal(total);
            response.setCode(OrderRetCode.SUCCESS.getCode());
            response.setMsg(OrderRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("OrderQueryServiceImpl.orderList Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }
}

