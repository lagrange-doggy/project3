package com.mall.shopping.services;

import com.mall.shopping.IHomeService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.PanelDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Fang
 */
@Slf4j
@Component
@Service
public class HomePageServiceImpl implements IHomeService {
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    PanelMapper panelMapper;
    @Autowired
    PanelContentMapper contentMapper;

    @Autowired
    ContentConverter contentConverter;

    @Override
    public HomePageResponse homepage() {
        HomePageResponse homePageResponse = new HomePageResponse();
        //先获得PanelId数据，筛选条件为position为0，因为要显示在首页
        Integer[] panelIds = panelMapper.selectAllPanelIdByPosition(0);
        List<Panel> panelList = new ArrayList<>();
        List<PanelContentItem> panelContentItems = new ArrayList<>();
        Set<PanelDto> panelDtoList = new HashSet<>();
        for (int i = 0; i < panelIds.length; i++) {
            Integer id = panelIds[i];
            //使用老师给的mapper来查询
            List<Panel> panels = panelMapper.selectPanelContentById(id);
            //取其中一条放入panelList中，因为其他的都是重复的，除了productId不同，可是我们此时不需要，因为下面查询的数据中也有productId
            panelList.add(panels.get(0));
            //再去获取PanelContentItem，根据panel_id去取多个商品信息以及panel_content中的信息
            panelContentItems = contentMapper.selectPanelContentAndProductWithPanelId(id);
            //把取到的内容放入panelList中成员的对应内容中，即Panel的成员panelContentItems
            panelList.get(i).setPanelContentItems(panelContentItems);
            //然后转换一下，里面的panelContentItems也会自动转换的
            panelDtoList.add(contentConverter.panen2Dto(panelList.get(i)));
        }
        homePageResponse.setPanelContentItemDtos(panelDtoList);
        homePageResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        homePageResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return homePageResponse;
    }
}
