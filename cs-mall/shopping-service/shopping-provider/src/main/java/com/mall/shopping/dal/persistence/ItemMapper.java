package com.mall.shopping.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.shopping.dal.entitys.Item;

import java.util.List;

import com.mall.shopping.dto.ProductDetailDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ItemMapper extends TkMapper<Item> {

    List<Item> selectItemFront(@Param("cid") Long cid,
                                 @Param("orderCol") String orderCol,@Param("orderDir") String orderDir,
                                 @Param("priceGt") Integer priceGt,@Param("priceLte") Integer priceLte);

    @Select("select item_desc from `project3.tb_item_desc` where item_id =#{item_id}")
    String SelectItemDescByid(@Param("item_id") Long id);

    @Select("select pic_url from `project3.tb_panel_content` where iproduct_id =#{iproduct_id}")
    List<String> SelectImageById(@Param("iproduct_id") Long id);


    @Select("select id as productId,price as salePrice, title as productName,sell-point as subTitle,image as productImageBig ,num as limitNum from `project3.tb_item` where id =#{id}")
    ProductDetailDto SelectItemById(@Param("id") Long id);

}