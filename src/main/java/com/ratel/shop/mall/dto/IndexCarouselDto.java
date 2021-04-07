
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页轮播图
 */
@Data
public class IndexCarouselDto implements Serializable {

    private String carouselUrl;

    private String redirectUrl;
}
