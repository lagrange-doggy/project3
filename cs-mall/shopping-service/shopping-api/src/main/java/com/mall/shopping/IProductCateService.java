package com.mall.shopping;

import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.RecommendResponse;
import com.mall.shopping.dto.ShoppingGoodsVO;

import java.util.List;


/**
 * Created by ciggar on 2019/8/8
 * 21:38.
 */
public interface IProductCateService {
    /**
     * 获取所有产品分类
     * @param request
     * @return
     */
    AllProductCateResponse getAllProductCate(AllProductCateRequest request);

    List<ShoppingGoodsVO> SelectGoodsListByPageAndSizeAndSort(Integer page, Integer size, String sort,
                                                              Integer priceGt, Integer priceLte,Integer total);

    Integer countByPriceGtAndPriceLte(Integer priceGt, Integer priceLte);

    Integer deleteItemGoodsById(Integer id);

    RecommendResponse queryRecomment();

    Integer deleteCarGoodsById(Integer uid, Integer pid);

}
