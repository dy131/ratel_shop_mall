
package com.ratel.shop.mall.controller.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品详情
 */
@Data
public class GoodsDetailDto implements Serializable {

    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private String[] goodsCarouselList;

    private Integer sellingPrice;

    private Integer originalPrice;

    private String goodsDetailContent;
}
