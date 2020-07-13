package com.mall.shopping.services.cache;

import com.mall.shopping.IShoppingNavigationService;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.ShoppingNavigationVO;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 杨
 * @create 2020-07-13 0:10
 */
@Service
public class ShoppingNavigationServiceImpl implements IShoppingNavigationService {

    @Autowired
    PanelMapper panelMapper;

    @Override
    public List<ShoppingNavigationVO> getPanelList() {
        return panelMapper.selectPanelListByPanerId();
    }
}
