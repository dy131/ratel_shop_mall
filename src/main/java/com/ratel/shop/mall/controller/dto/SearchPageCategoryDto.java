
package com.ratel.shop.mall.controller.dto;

import com.ratel.shop.mall.entity.GoodsCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索页面分类数据
 */
@Data
public class SearchPageCategoryDto implements Serializable {

    private String firstLevelCategoryName;

    private List<GoodsCategory> secondLevelCategoryList;

    private String secondLevelCategoryName;

    private List<GoodsCategory> thirdLevelCategoryList;

    private String currentCategoryName;
}
