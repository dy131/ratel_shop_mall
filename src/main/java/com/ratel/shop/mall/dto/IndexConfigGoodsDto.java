
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页配置商品
 */
@Data
public class IndexConfigGoodsDto implements Serializable {

    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private String tag;
}
