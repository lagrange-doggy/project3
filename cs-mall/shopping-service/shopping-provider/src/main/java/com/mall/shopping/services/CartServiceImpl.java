package com.mall.shopping.services;

import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.CartItemConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ponfy
 * @create 2020-07-12-16:56
 */
@Service
@Component
@Slf4j
public class CartServiceImpl implements ICartService {
    static final String Key_JOINT = "cart_user_id_";

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ItemMapper itemMapper;

    /**
     *  Fang
     *  查看当前用户的商品信息
     */
    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        CartListByIdResponse response = new CartListByIdResponse();
        try {
            //判空
            request.requestCheck();
            Long userId = request.getUserId();
            String key = Key_JOINT + userId;

            RBucket<Object> bucket = redissonClient.getBucket(key);
            List<CartProductDto> cartList = (List<CartProductDto>) bucket.get();
            response.setCartProductDtos(cartList);
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("CartServiceImpl.getCartListById occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    /**
     *  Fang
     *  添加商品到购物车
     */
    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        AddCartResponse response = new AddCartResponse();
        try {
            //判空
            request.requestCheck();
            //获取request中的数据
            Long userId = request.getUserId();
            String key = Key_JOINT + userId;
            Long itemId = request.getItemId();
            Integer num = request.getNum();

            //根据itemId去获取商品信息
            Item item = itemMapper.selectById(itemId);
            if(item == null){
                //一定会存在的，不然商品不会显示出来的
            }
            //把item转换成对应的CartProductDTo
            CartProductDto cartProductDto = CartItemConverter.item2Dto(item);
            //设置对应的productNum 和 check
            cartProductDto.setProductNum(num.longValue());
            cartProductDto.setChecked("false");

            //判断该userId作为key的redis是否存在
            RBucket<Object> bucket = redissonClient.getBucket(key);
            List<CartProductDto> cartList = (List<CartProductDto>) bucket.get();
            if(cartList == null){
                //不存在
                cartList = new ArrayList<>();
                cartList.add(cartProductDto);
                //放入redis
                bucket.set(cartList);
                response.setCode(ShoppingRetCode.SUCCESS.getCode());
                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
                return response;
            }
            //在redis中已存在
            //要判断要添加的商品是否已存在其中
            //测试foreach中修改num后不生效
            for (int i = 0; i < cartList.size(); i++) {
                if(cartList.get(i).getProductId().equals(itemId)){
                    cartList.get(i).setProductNum(cartList.get(i).getProductNum() + num);
                    bucket.set(cartList);
                    response.setCode(ShoppingRetCode.SUCCESS.getCode());
                    response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
                    return response;
                    //此处有个小瑕疵，好像新加入的商品不会显示在第一个
                }
            }
            //若商品不存在,直接添加在cartList中， 然后set
            cartList.add(cartProductDto);
            bucket.set(cartList);
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("CartServiceImpl.addToCart occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
        //代码有点冗余，有待优化
    }

    /**
     * 刘鹏飞
     * 更新购物车
     * @param request
     * @return
     */
    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
        UpdateCartNumResponse response = new UpdateCartNumResponse();
        try {
            request.requestCheck();
            //拼接key
            String key = Key_JOINT + request.getUserId();
            RBucket< List<CartProductDto>> bucket = redissonClient.getBucket(key);
            List<CartProductDto> cart = bucket.get();
            for (CartProductDto cartProductDto : cart) {
                if(cartProductDto.getProductId().equals(request.getProductId())){
                    cartProductDto.setChecked(request.getChecked());
                    cartProductDto.setProductNum(Long.valueOf(request.getProductNum()));
                }
            }
            bucket.set(cart);
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("CartServiceImpl.updateCartNum Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        return null;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        return null;
    }

    /**
     *  Fang
     *  删除购物车中选中的商品
     */
    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        DeleteCheckedItemResposne response = new DeleteCheckedItemResposne();
        try {
            request.requestCheck();
            Long userId = request.getUserId();
            String key = Key_JOINT + userId;

            RBucket<Object> bucket = redissonClient.getBucket(key);
            List<CartProductDto> cartList = (List<CartProductDto>) bucket.get();
            //这里不能用forEach循环来删除，可能会陷入死循环等异常
            //使用Iterator迭代器
            Iterator<CartProductDto> iterator = cartList.iterator();
            while (iterator.hasNext()){
                CartProductDto next = iterator.next();
                if(next.getChecked().equals("true")){
                    iterator.remove();
                }
            }
            bucket.set(cartList);
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("CartServiceImpl.addToCart occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }
}
