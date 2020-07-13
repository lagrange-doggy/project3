package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.UpdateCartNumRequest;
import com.mall.shopping.dto.UpdateCartNumResponse;
import com.mall.user.annotation.Anoymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ponfy
 * @create 2020-07-12-17:00
 */
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
}
