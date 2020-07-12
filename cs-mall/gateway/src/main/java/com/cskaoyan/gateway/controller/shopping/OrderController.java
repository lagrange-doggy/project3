package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.CreateOrderResponse;
import com.mall.user.intercepter.TokenIntercepter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.dubbo.config.annotation.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author 韩
 * @create 2020-07-13 0:06
 */
@RestController
@RequestMapping("shopping")
@Slf4j
@Api(tags = "OrderController",description = "订单控制层")
public class OrderController {
    @Reference(timeout = 3000,check = false)
    private OrderCoreService orderCoreService;

    @Reference(timeout = 3000,check = false)
    private OrderQueryService orderQueryService;

    /**
     * 创建订单
     */
    @PostMapping("/order")
    @ApiOperation("创建订单")
    public ResponseData order(@RequestBody CreateOrderRequest request, HttpServletRequest servletRequest){
        String  userInfo = (String)servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());
        request.setUserId(uid);
        //设置uuiqueKey
        request.setUniqueKey(UUID.randomUUID().toString());
        CreateOrderResponse response = orderCoreService.createOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response.getOrderId());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }
}
