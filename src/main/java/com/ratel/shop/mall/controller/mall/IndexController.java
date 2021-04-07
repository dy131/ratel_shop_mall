
package com.ratel.shop.mall.controller.mall;

import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.IndexConfigTypeEnum;
import com.ratel.shop.mall.dto.IndexCarouselDto;
import com.ratel.shop.mall.dto.IndexCategoryDto;
import com.ratel.shop.mall.dto.IndexConfigGoodsDto;
import com.ratel.shop.mall.service.CarouselService;
import com.ratel.shop.mall.service.CategoryService;
import com.ratel.shop.mall.service.IndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private CarouselService carouselService;

    @Resource
    private IndexConfigService indexConfigService;

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<IndexCategoryDto> categories = categoryService.queryIndexCategories();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        // 分类数据
        request.setAttribute("categories", categories);

        List<IndexCarouselDto> carousels = carouselService.queryIndexCarousels(Constants.INDEX_CAROUSEL_NUMBER);
        // 轮播图
        request.setAttribute("carousels", carousels);

        List<IndexConfigGoodsDto> hotGoods = indexConfigService.queryIndexConfigGoodsByType(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(),
                Constants.INDEX_GOODS_HOT_NUMBER);
        // 热销商品
        request.setAttribute("hotGoods", hotGoods);

        List<IndexConfigGoodsDto> newGoods = indexConfigService.queryIndexConfigGoodsByType(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(),
                Constants.INDEX_GOODS_NEW_NUMBER);
        // 新品
        request.setAttribute("newGoods", newGoods);

        List<IndexConfigGoodsDto> recommendGoods = indexConfigService.queryIndexConfigGoodsByType(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(),
                Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        // 推荐商品
        request.setAttribute("recommendGoods", recommendGoods);

        request.setAttribute("path", "index");
        return "mall/index";
    }
}
