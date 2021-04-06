
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.controller.dto.IndexCarouselDto;
import com.ratel.shop.mall.entity.Carousel;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;

import java.util.List;

public interface CarouselService {

    /**
     * 首页轮播图
     */
    List<IndexCarouselDto> queryIndexCarousels(int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);


}
