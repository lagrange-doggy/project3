package com.mall.shopping.services;

import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/* *
@author  Walker-胡
@create 2020-07-12 15:57
*/
@Slf4j
@Component
@Service
public class IProductServiceImpl implements IProductService {
    @Autowired
    ItemMapper itemMapper;
    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        ProductDetailResponse response=new ProductDetailResponse ();
        try {
            request.requestCheck ();
            //用ProductDetailDto类型存放要返回的数据
            ProductDetailDto product = new ProductDetailDto ();
            //查找detail
            String detail=itemMapper.SelectItemDescByid(request.getId ());
            //查找显示小图的数据
            List<String> productImageSmall=itemMapper.SelectImageById(request.getId ());
            //查找商品详情数据
            product=itemMapper.SelectItemById(request.getId ());

            //将缺少的成员变量加入商品对象中
            product.setDetail (detail);
            product.setProductImageSmall (productImageSmall);

            response.setProductDetailDto (product);
            response.setCode (ShoppingRetCode.SUCCESS.getCode ());
            response.setMsg (ShoppingRetCode.SUCCESS.getMessage ());

        } catch (Exception e) {
            log.error("IProductServiceImpl.getProductDetail occur Exception :" +e);
        }

        return response;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        return null;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        return null;
    }
}
