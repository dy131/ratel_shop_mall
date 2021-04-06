
package com.ratel.shop.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ratel.shop.mall.common.CategoryLevelEnum;
import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.controller.dto.IndexCategoryDto;
import com.ratel.shop.mall.controller.dto.SearchPageCategoryDto;
import com.ratel.shop.mall.controller.dto.SecondLevelCategoryDto;
import com.ratel.shop.mall.controller.dto.ThirdLevelCategoryDto;
import com.ratel.shop.mall.entity.GoodsCategory;
import com.ratel.shop.mall.mapper.GoodsCategoryMapper;
import com.ratel.shop.mall.service.CategoryService;
import com.ratel.shop.mall.util.BeanUtil;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public List<IndexCategoryDto> queryIndexCategories() {
        List<IndexCategoryDto> indexCategoryDtoList = new ArrayList<>();
        // 获取一级分类的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.queryCategoryByParentIdAndLevel(Collections.singletonList(0L),
                CategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (CollectionUtils.isEmpty(firstLevelCategories)) {
            return null;
        }
        List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
        // 获取二级分类的数据
        List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.queryCategoryByParentIdAndLevel(firstLevelCategoryIds, CategoryLevelEnum.LEVEL_TWO.getLevel(),
                0);
        if (!CollectionUtils.isEmpty(secondLevelCategories)) {
            return null;
        }
        List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
        // 获取三级分类的数据
        List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.queryCategoryByParentIdAndLevel(secondLevelCategoryIds, CategoryLevelEnum.LEVEL_THREE.getLevel(),
                0);
        if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
            return null;
        }
        // 根据parentId将thirdLevelCategories分组
        Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
        List<SecondLevelCategoryDto> secondLevelCategoryDtoList = new ArrayList<>();
        // 处理二级分类
        for (GoodsCategory goodsCategory : secondLevelCategories) {
            SecondLevelCategoryDto secondLevelCategoryDto = new SecondLevelCategoryDto();
            BeanUtil.copyProperties(goodsCategory, secondLevelCategoryDto);
            // 二级分类下有数据
            if (thirdLevelCategoryMap.containsKey(goodsCategory.getCategoryId())) {
                // 三级分类放入二级分类中
                List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(goodsCategory.getCategoryId());
                secondLevelCategoryDto.setThirdLevelCategoryDtoList((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryDto.class)));
                secondLevelCategoryDtoList.add(secondLevelCategoryDto);
            }
        }
        // 处理一级分类
        if (!CollectionUtils.isEmpty(secondLevelCategoryDtoList)) {
            return null;
        }
        // 根据parentId将secondLevelCategories 分组
        Map<Long, List<SecondLevelCategoryDto>> secondLevelCategoryDtoMap = secondLevelCategoryDtoList.stream().collect(groupingBy(SecondLevelCategoryDto::getParentId));
        for (GoodsCategory firstCategory : firstLevelCategories) {
            IndexCategoryDto indexCategoryDto = new IndexCategoryDto();
            BeanUtil.copyProperties(firstCategory, indexCategoryDto);
            if (secondLevelCategoryDtoMap.containsKey(firstCategory.getCategoryId())) {
                List<SecondLevelCategoryDto> tempGoodsCategories = secondLevelCategoryDtoMap.get(firstCategory.getCategoryId());
                indexCategoryDto.setSecondLevelCategoryDtoList(tempGoodsCategories);
                indexCategoryDtoList.add(indexCategoryDto);
            }
        }
        return indexCategoryDtoList;
    }

    @Override
    public SearchPageCategoryDto queryCategoriesByCategoryId(Long categoryId) {
        SearchPageCategoryDto searchPageCategoryDto = new SearchPageCategoryDto();
        GoodsCategory goodsCategory = goodsCategoryMapper.selectById(categoryId);
        if (goodsCategory != null && goodsCategory.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            // 二级分类
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper.selectById(goodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == CategoryLevelEnum.LEVEL_TWO.getLevel()) {
                // 三级分类
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.queryCategoryByParentIdAndLevel(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryDto.setCurrentCategoryName(goodsCategory.getCategoryName());
                searchPageCategoryDto.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryDto.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryDto;
            }
        }
        return null;
    }

    @Override
    public PageResult getCategoryPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }



    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.queryCategoryByParentIdAndLevel(parentIds, categoryLevel, 0);//0代表查询所有
    }
}
