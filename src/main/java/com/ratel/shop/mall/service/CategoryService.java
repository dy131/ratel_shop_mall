
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.dto.IndexCategoryDto;
import com.ratel.shop.mall.dto.SearchPageCategoryDto;
import com.ratel.shop.mall.entity.GoodsCategory;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 首页商品分类数据
     */
    List<IndexCategoryDto> queryIndexCategories();

    /**
     * 通过分类ID查询分类数据
     */
    SearchPageCategoryDto queryCategoriesByCategoryId(Long categoryId);

    PageResult getCategoryPage(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);


    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
