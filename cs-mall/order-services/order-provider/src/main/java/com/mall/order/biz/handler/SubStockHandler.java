package com.mall.order.biz.handler;


import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.CartProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 扣减库存处理器
 * @Author： wz
 * @Date: 2019-09-16 00:03
 **/
@Component
@Slf4j
public class SubStockHandler extends AbstractTransHandler {

    @Autowired
    private StockMapper stockMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @Transactional
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        List<CartProductDto> dtoList = createOrderContext.getCartProductDtoList();
        List<Long> productIds = createOrderContext.getBuyProductIds();
        if (CollectionUtils.isEmpty(productIds)) {
            productIds = dtoList.stream().map(u -> u.getProductId()).collect(Collectors.toList());
        }
        productIds.sort(Long::compareTo);
        //锁定库存
        List<Stock> stockList = stockMapper.findStocksForUpdate(productIds);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new BizException("库存未初始化！");
        }
        if (stockList.size() != productIds.size()) {
            throw new BizException("部分商品库存未初始化！");
        }
        //扣减库存
        for (CartProductDto cartProductDto : dtoList) {
            Long productId = cartProductDto.getProductId();
            Long productNum = cartProductDto.getProductNum();

            Long trueProductNum;
            //productNum不能超出我们限购的数量
            Long limitNum = Long.valueOf(stockMapper.selectLimitNumByProductId(productId));
            if (productNum > limitNum) {
                trueProductNum = limitNum;
            }else {
                trueProductNum = productNum;
            }

            Stock stock = new Stock();
            stock.setItemId(productId);
            stock.setLockCount(trueProductNum.intValue());
            stock.setStockCount(-productNum);

            stockMapper.updateStock(stock);
        }
        return true;
    }
}
