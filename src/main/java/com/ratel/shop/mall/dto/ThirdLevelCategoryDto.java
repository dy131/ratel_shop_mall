
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页分类数据VO
 */
@Data
public class ThirdLevelCategoryDto implements Serializable {

    private Long categoryId;

    private Byte categoryLevel;

    private String categoryName;
}
