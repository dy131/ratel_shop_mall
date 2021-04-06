
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.controller.dto.IndexConfigGoodsDto;
import com.ratel.shop.mall.entity.IndexConfig;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;

import java.util.List;

public interface IndexConfigService {

    /**
     * 首页商品配置
     */
    List<IndexConfigGoodsDto> queryIndexConfigGoodsByType(int configType, int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);


    Boolean deleteBatch(Long[] ids);
}
