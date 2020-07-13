package com.mall.shopping.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.shopping.dal.entitys.Panel;

import java.util.List;

import com.mall.shopping.dto.ShoppingNavigationVO;
import org.apache.ibatis.annotations.Param;

public interface PanelMapper extends TkMapper<Panel> {

    List<Panel> selectPanelContentById(@Param("panelId")Integer panelId);

    Panel selectByName(@Param("name") String name);


    List<ShoppingNavigationVO> selectPanelListByPanerId();

    //homepage
    Integer[] selectAllPanelIdByPosition(@Param("position") Integer position);

}