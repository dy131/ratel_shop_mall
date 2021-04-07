
package com.ratel.shop.mall.service.impl;

import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.IndexCarouselDto;
import com.ratel.shop.mall.entity.Carousel;
import com.ratel.shop.mall.mapper.CarouselMapper;
import com.ratel.shop.mall.service.CarouselService;
import com.ratel.shop.mall.util.BeanUtil;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    @Override
    public List<IndexCarouselDto> queryIndexCarousels(int number) {
        List<IndexCarouselDto> indexCarouselDtoList = new ArrayList<>(number);
        List<Carousel> carouselList = carouselMapper.queryCarouselByNum(number);
        if (!CollectionUtils.isEmpty(carouselList)) {
            indexCarouselDtoList = BeanUtil.copyList(carouselList, IndexCarouselDto.class);
        }
        return indexCarouselDtoList;
    }

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        List<Carousel> carousels = carouselMapper.findCarouselList(pageUtil);
        int total = carouselMapper.getTotalCarousels(pageUtil);
        PageResult pageResult = new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (carouselMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return carouselMapper.deleteBatch(ids) > 0;
    }


}
