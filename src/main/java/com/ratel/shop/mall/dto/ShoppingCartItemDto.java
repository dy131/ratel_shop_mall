
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 购物车页面购物项
 */
@Data
public class ShoppingCartItemDto implements Serializable {

    private Long cartItemId;

    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
