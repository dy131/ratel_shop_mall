
package com.ratel.shop.mall.controller.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页分类数据
 */
@Data
public class IndexCategoryDto implements Serializable {

    private Long categoryId;

    private Byte categoryLevel;

    private String categoryName;

    private List<SecondLevelCategoryDto> secondLevelCategoryDtoList;
}
