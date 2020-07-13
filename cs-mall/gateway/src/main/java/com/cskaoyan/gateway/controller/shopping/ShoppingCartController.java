package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.IProductService;
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
    @Reference(timeout = 3000, check = false)
    IProductCateService productCateService;

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

    /*
     *胡
     * 删除购物车的商品
     *
     * */
    @Anoymous
    @DeleteMapping("/cars/{uid}/{pid}")
    public ResponseData cars(@PathVariable("uid") Integer uid,@PathVariable("pid") Integer pid) {
        //根据id删除
        Integer delete = productCateService.deleteCarGoodsById(uid,pid);
        if (delete == 0) {
            //如果没有返回失败
            log.info("查询错误uid pid，未能删除" + uid +" "+pid);
            return new ResponseUtil().setErrorMsg("删除失败，uid = " +uid+"pid = " + pid);
        } else {
            log.info("正常删除购物车商品，商品pid=" + pid +"用户id="+uid);
            return new ResponseUtil().setData("删除成功，pid = " + pid+"uid ="+uid);
        }
    }


    /**
     * 胡
     * 显示商品详情
     * <p>
     * 返回参数示例：
     * { "success":true, "message":"success", "code":200,
     * "result":{
     *         "productId":100057501,
     *          "salePrice":149, "productName":"Smartisan T恤 毕加索", "subTitle":"", "limitNum":100,
     *           "productImageBig":"https://resource.smartisan.com/resource/e9cd634b62470713f6b9c5a6
     *              065f4a10.jpg",
     *            "detail":"",
     *            "productImageSmall":[ "https://resource.smartisan.com/resource
     *                      /e9cd634b62470713f6b9c5a6065f4a10.jpg", "https://resource.smartisan.com/resource/2ea9
     *                      73de25dffab6373dbe5e343f76c8.jpg", "https://resource.smartisan.com/resource/57c12d9b6
     *                      788d005341fe4aefd209fab.jpg", "https://resource.smartisan.com/resource/25fb00a88fe6ab
     *                      abcd580a2cf0a14032.jpg", "https://resource.smartisan.com/resource/bab385bd6811378389
     *                      a12d7b7254ed7e.jpg" ]
     *              },
     * "timestamp":1587791351079 }
     */
    @Reference(timeout = 3000,check = false)
    IProductService iProductService;

    @Anoymous
    @GetMapping("/product/{id}")
    public ResponseData product(ProductDetailRequest request) {
        ProductDetailResponse response=iProductService.getProductDetail (request);
        if(response.getCode ().equals (ShoppingRetCode.SUCCESS.getCode ())){
            return new ResponseUtil ().setData (response);
        }

        return new ResponseUtil().setErrorMsg (response.getMsg ());

    }
}
