package com.mall.shopping;

import com.mall.shopping.dto.ShoppingNavigationVO;

import java.util.List;

/**
 * @author 杨
 * @create 2020-07-13 0:07
 */
public interface IShoppingNavigationService {
    List<ShoppingNavigationVO> getPanelList();
}
