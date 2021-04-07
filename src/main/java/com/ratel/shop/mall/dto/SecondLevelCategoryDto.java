
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页分类数据(第二级)
 */
@Data
public class SecondLevelCategoryDto implements Serializable {

    private Long categoryId;

    private Long parentId;

    private Byte categoryLevel;

    private String categoryName;

    private List<ThirdLevelCategoryDto> thirdLevelCategoryDtoList;
}
