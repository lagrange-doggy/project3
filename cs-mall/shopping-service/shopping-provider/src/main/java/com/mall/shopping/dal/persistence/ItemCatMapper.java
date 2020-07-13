package com.mall.shopping.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dto.ShoppingGoodsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatMapper extends TkMapper<ItemCat> {

    List<ShoppingGoodsVO> selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLte(@Param("page") Integer page,
                                                                                   @Param("size") Integer size,
                                                                                   @Param("priceGt") Integer priceGt,
                                                                                   @Param("priceLte") Integer priceLte);

    List<ShoppingGoodsVO> selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLteSortOrder(@Param("page") Integer page,
                                                                                            @Param("size") Integer size,
                                                                                            @Param("sort") String sort,
                                                                                            @Param("priceGt") Integer priceGt,
                                                                                            @Param("priceLte") Integer priceLte);

    List<ShoppingGoodsVO> selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLteSortReversedOrder(@Param("page") Integer page,
                                                                                                    @Param("size") Integer size,
                                                                                                    @Param("sort") String sort,
                                                                                                    @Param("priceGt") Integer priceGt,
                                                                                                    @Param("priceLte") Integer priceLte);

    Integer countByPriceGtAndPriceLte(@Param("priceGt") Integer priceGt, @Param("priceLte") Integer priceLte);

}
