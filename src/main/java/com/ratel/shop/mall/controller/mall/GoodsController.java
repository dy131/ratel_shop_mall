
package com.ratel.shop.mall.controller.mall;

import cn.hutool.core.util.StrUtil;
import com.ratel.shop.mall.common.BusinessException;
import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.GoodsDetailDto;
import com.ratel.shop.mall.dto.SearchPageCategoryDto;
import com.ratel.shop.mall.entity.Goods;
import com.ratel.shop.mall.service.CategoryService;
import com.ratel.shop.mall.service.GoodsService;
import com.ratel.shop.mall.util.BeanUtil;
import com.ratel.shop.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (params.get("page") == null) params.put("page", 1);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

        // 封装分类数据
        Object obj = params.get("categoryId");
        final String categoryId = obj == null ? null : obj.toString();
        if (!StrUtil.isBlank(categoryId)) {
            SearchPageCategoryDto searchPageCategoryDto = categoryService.queryCategoriesByCategoryId(Long.valueOf(categoryId));
            if (searchPageCategoryDto != null) {
                request.setAttribute("categoryId", categoryId);
                request.setAttribute("searchPageCategoryDto", searchPageCategoryDto);
            }
        }

        // 封装参数供前端回显
        if (params.containsKey("orderBy") && !StrUtil.isBlank(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        // 对keyword做过滤去掉空格
        if (params.containsKey("keyword") && !StrUtil.isBlank((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);

        // 搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);

        // 封装商品数据
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", goodsService.searchPageGoods(pageQueryUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        Goods goods = goodsService.queryGoodByGoodId(goodsId);
        if (goods == null) {
            BusinessException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            BusinessException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
        BeanUtil.copyProperties(goods, goodsDetailDto);
        goodsDetailDto.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetailDto", goodsDetailDto);
        return "mall/detail";
    }

}
