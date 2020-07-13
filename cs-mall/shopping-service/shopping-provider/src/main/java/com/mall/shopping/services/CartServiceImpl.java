package com.mall.shopping.services;

import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        return null;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        return null;
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

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        return null;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }
}
