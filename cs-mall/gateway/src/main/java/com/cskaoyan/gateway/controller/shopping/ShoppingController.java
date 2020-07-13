package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IHomeService;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
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

    @Reference(timeout = 3000,check = false)
    IHomeService homeService;
    

    /**
     *  Fang
     *  显示主页信息
     *  涉及item，panel，panel_content表
     * @return
     */
    @Anoymous
    @GetMapping("/homepage")
    public ResponseData homepageList(){
        HomePageResponse homePageResponse = homeService.homepage();
        if(homePageResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(homePageResponse.getPanelContentItemDtos());
        }
        return new ResponseUtil<>().setErrorMsg(homePageResponse.getMsg());
    }

    /**
     * 刘鹏飞
     * 获取商品所有分类
     * @param request
     * @return
     */
    @GetMapping("categories")
    @Anoymous//测试用
    public ResponseData categories(AllProductCateRequest request) {
        AllProductCateResponse allProductCate = productCateService.getAllProductCate(request);
        if(!allProductCate.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(Integer.valueOf(allProductCate.getCode()),allProductCate.getMsg());
        }
        return new ResponseUtil<>().setData(allProductCate.getProductCateDtoList());
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
    public ResponseData<ShoppingGoodsResultVO> goodsList(Integer page, Integer size, String sort,
                                                         Integer priceGt, Integer priceLte) {
        //满足最高最佳的商品合计多少个
        Integer total = productCateService.countByPriceGtAndPriceLte(priceGt, priceLte);
        //获取商品结果集
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
     * 尚政宇
     *
     * @return 返回热门推荐
     */
    @Anoymous
    @RequestMapping("recommend")
    public ResponseData recommend() {
        RecommendResponse recommendResponse = productCateService.queryRecomment();
        return new ResponseUtil<>().setData(recommendResponse.getPanelContentItemDtos());
    }
}
