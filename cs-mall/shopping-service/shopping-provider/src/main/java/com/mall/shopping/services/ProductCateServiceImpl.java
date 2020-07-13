package com.mall.shopping.services;

import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemCatMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.services.cache.CacheManager;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author ponfy
 * @create 2020-07-11-17:32
 */
@Slf4j
@Component
@Service
public class ProductCateServiceImpl implements IProductCateService {

    @Autowired
    ItemCatMapper itemCatMapper;

    /**
     * 刘鹏飞
     * 获取商品分类
     *
     * @param request
     * @return
     */
    @Override
    public AllProductCateResponse getAllProductCate(AllProductCateRequest request) {
        AllProductCateResponse response = new AllProductCateResponse();
        try {
            //先从缓存中获取(待实现)
//            CacheManager cacheManager = new CacheManager();
//            String allProductCate = cacheManager.checkCache("allProductCate");
            request.requestCheck();
            //从数据库中获取
            Example example = new Example(ItemCat.class);
            example.setOrderByClause(request.getSort());
            List<ItemCat> itemCats = itemCatMapper.selectByExample(example);
            //转换Bean
            response = getResponse(itemCats);
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error(" ProductCateServiceImpl.AllProductCateResponse Occur Exception :" +e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    /**
     * 韩
     * 分⻚查询商品列表
     */
    @Override
    public List<ShoppingGoodsVO> SelectGoodsListByPageAndSizeAndSort(Integer page, Integer size,
                                                                     String sort, Integer priceGt,
                                                                     Integer priceLte, Integer total) {
        // 先处理page和size 使数据达到需要的情况
        int Pagei = page - 1;
        if (Pagei > 0) {
            Pagei = (page - 1) * size;
        }
        Integer truePage = Pagei;
        // 先判断排序方式
        if ("".equals(sort) || sort == null) {
            return itemCatMapper.selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLte(truePage,
                    size, priceGt, priceLte);
        } else if ("1".equals(sort)) {//从小到大
            return itemCatMapper.selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLteSortOrder(truePage,
                    size, sort, priceGt, priceLte);
        } else {//从大到小
            return itemCatMapper.selectGoodsListByPageAndSizeAndSortAndPriceGtAndPriceLteSortReversedOrder(truePage,
                    size, sort, priceGt, priceLte);
        }
    }

    @Override
    public Integer countByPriceGtAndPriceLte(Integer priceGt, Integer priceLte) {
        return itemCatMapper.countByPriceGtAndPriceLte(priceGt, priceLte);
    }

    /**
     * 韩
     * 删除购物车指定商品
     */
    @Override
    public Integer deleteItemGoodsById(Integer id) {
        return itemCatMapper.deleteItemGoodsById(id);
    }

    @Autowired
    PanelMapper panelMapper;

    @Autowired
    PanelContentMapper panelContentMapper;

    @Autowired
    ContentConverter contentConverter;


     @Override
     public RecommendResponse queryRecomment() {
         String name = "热门推荐";
         Panel panel = panelMapper.selectByName(name);
         Example example = new Example(PanelContent.class);
         example.createCriteria().andEqualTo("panelId", panel.getId());
         List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(panel.getId());
         RecommendResponse recommendResponse = new RecommendResponse();
         HashSet<PanelDto> panelContentItemDtos = new HashSet<>();
         panel.setPanelContentItems(panelContentItems);
         panelContentItemDtos.add(contentConverter.panen2Dto(panel));
         recommendResponse.setPanelContentItemDtos(panelContentItemDtos);
         return recommendResponse;
     }


    private AllProductCateResponse getResponse(List<ItemCat> itemCats) {
        List<ProductCateDto> list = new ArrayList<>();
        for (ItemCat itemCat : itemCats) {
            ProductCateDto productCateDto = new ProductCateDto();
            BeanUtils.copyProperties(itemCat, productCateDto);
            list.add(productCateDto);
        }
        AllProductCateResponse allProductCateResponse = new AllProductCateResponse();
        allProductCateResponse.setProductCateDtoList(list);
        return allProductCateResponse;
    }
}
