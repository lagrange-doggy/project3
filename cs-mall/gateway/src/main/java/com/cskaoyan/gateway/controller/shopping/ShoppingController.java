package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.ShoppingGoodsResultVO;
import com.mall.shopping.dto.ShoppingGoodsVO;
import com.mall.user.annotation.Anoymous;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ponfy
 * @create 2020-07-11-17:00
 */
@Slf4j
@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Reference(timeout = 3000, check = false)
    IProductCateService productCateService;


    @GetMapping("categories")
    public ResponseData categories(AllProductCateRequest request) {
        AllProductCateResponse allProductCate = productCateService.getAllProductCate(request);
        return new ResponseUtil().setData(allProductCate);
    }

    /**
     * 韩
     * 分⻚查询商品列表
     * 入参列表：
     * page Integer ⻚码
     * size Integer 每⻚条数
     * sort String 是否排序
     * priceGt Integer 价格最⼩值
     * priceLte Integer 价格最⼤值
     * cid Long ⻚码
     * { "success":true, "message":"success", "code":200, "result":{ "data":[ {
     * "productId":100040607, "salePrice":2999, "productName":"坚果 3", "subTitle":"坚果 3 意外碎屏
     * 保修服务（碎屏险）", "picUrl":"https://resource.smartisan.com/resource/13e91511f6ba3227c
     * a5378fd2e93c54b.png" }, { "productId":100057601, "salePrice":149,
     * "productName":"Smartisan T恤 皇帝的新装", "subTitle":"", "picUrl":"https://resource.smartis
     * an.com/resource/d9586f7c5bb4578e3128de77a13e4d85.png" } ], "total":2 },
     * "timestamp":1587791665215 }
     * <p>
     * 出参列表：
     * success boolean 成功标记
     * message String 具体信息
     * code Integer 状态码
     * result String(JSON) 具体数据
     * timestamp Long 时间戳
     */
    @Anoymous
    @GetMapping("/goods")
    public ResponseData<ShoppingGoodsResultVO> goodsList(Integer page, Integer size, String sort, Integer priceGt, Integer priceLte) {
        Integer total = productCateService.countByPriceGtAndPriceLte(priceGt, priceLte);
        List<ShoppingGoodsVO> data = productCateService.SelectGoodsListByPageAndSizeAndSort(page, size,
                sort, priceGt, priceLte,total);
        //判断列表是否为空
        //创建返回结果数据对象
        ShoppingGoodsResultVO resultVO = new ShoppingGoodsResultVO();
        if (data.size() == 0) {
            //判断列表为空原因
            if (priceGt == null&&priceLte == null) {//最高最低都为空依然没有商品！
                log.info("由于系统异常，分⻚查询商品列表查询结果空集");
                return new ResponseUtil<ShoppingGoodsResultVO>().setErrorMsg("系统异常");
            }
            log.info("分⻚查询商品列表查询结果空集，查询区间不存在该价位商品");
            return new ResponseUtil<ShoppingGoodsResultVO>().setData(resultVO,"查询区间不存在该价位商品");
        }
        //列表非空 正常返回
        //取得满足条件商品数目
        resultVO.setData(data);
        resultVO.setTotal(total);
        //日志
        log.info("分⻚查询:\n" + data + "\t" + total);
        return new ResponseUtil<ShoppingGoodsResultVO>().setData(resultVO);
    }

    /**
     * 韩
     * 删除购物⻋中选中的商品（未完成）表格没弄清楚不知道哪个表 sql未完成
     * <p>
     * 返回参数示例：
     * {"success":true,"message":"success","code":200,"result":"成功","timestamp":1587795935665}
     */
    @Anoymous
    @DeleteMapping("/items/{id}")
    public ResponseData items(@PathVariable("id") Integer id) {
        //根据id删除
        Integer delete = productCateService.deleteItemGoodsById(id);
        if (delete == 0) {
            //如果没有返回失败
            log.info("查询错误id，未能删除" + id);
            return new ResponseUtil().setErrorMsg("删除失败，id = " + id);
        } else {
            log.info("正常删除购物车商品，商品id=" + id);
            return new ResponseUtil().setData("删除成功，id = " + id);
        }
    }
}
