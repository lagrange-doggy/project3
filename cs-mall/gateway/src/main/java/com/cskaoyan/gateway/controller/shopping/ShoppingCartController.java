package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.annotation.Anoymous;
import com.mall.user.intercepter.TokenIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ponfy
 * @create 2020-07-12-17:00
 */
@Slf4j
@RestController
@RequestMapping("shopping")
public class ShoppingCartController {

    @Reference(timeout = 5000,check = false)
    ICartService cartService;

    /**
     * 更新购物车
     * @return
     */
    @PutMapping("carts")
    @Anoymous
    public ResponseData updateCart(@RequestBody UpdateCartNumRequest updateCartRequest, HttpServletRequest req){
        UpdateCartNumResponse updateCartResponse = cartService.updateCartNum(updateCartRequest);
        if(!updateCartResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(Integer.valueOf(updateCartResponse.getCode()),updateCartResponse.getMsg());
        }
        return new ResponseUtil<>().setData(updateCartResponse);
    }

    /**
     *  Fang
     *  获得用户的购物车信息
     * @param request
     * @return
     */
    @GetMapping("/carts")
    public ResponseData getCartListById(HttpServletRequest request){
        String userInfo = (String) request.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(jsonObject.get("uid").toString());
        CartListByIdRequest cartListByIdRequest = new CartListByIdRequest();
        cartListByIdRequest.setUserId(uid);
        CartListByIdResponse response = cartService.getCartListById(cartListByIdRequest);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response.getCartProductDtos());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

    /**
     *  Fang
     * 添加商品到购物车
     * @param addToCartRequest
     * @param request
     * @return
     */
    @PostMapping("/carts")
    public ResponseData addToCart(@RequestBody AddCartRequest addToCartRequest, HttpServletRequest request){
        AddCartResponse response = cartService.addToCart(addToCartRequest);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            //只用返回“成功”
            return new ResponseUtil<>().setData(response.getMsg());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }


    /**
     *  Fang
     *  删除购物车中选中的商品
     * @param id userId
     * @return
     */
    @DeleteMapping("/items/{id}")
    public ResponseData deleteCheckedItem(@PathVariable("id") Long id) {
        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
        request.setUserId(id);
        DeleteCheckedItemResposne response = cartService.deleteCheckedItem(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            //只用返回“成功”信息
            return new ResponseUtil<>().setData(response.getMsg());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }
}
