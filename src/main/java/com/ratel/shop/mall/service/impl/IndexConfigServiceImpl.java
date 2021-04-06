
package com.ratel.shop.mall.service.impl;

import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.controller.dto.IndexConfigGoodsDto;
import com.ratel.shop.mall.entity.Goods;
import com.ratel.shop.mall.entity.IndexConfig;
import com.ratel.shop.mall.mapper.GoodsMapper;
import com.ratel.shop.mall.mapper.IndexConfigMapper;
import com.ratel.shop.mall.service.IndexConfigService;
import com.ratel.shop.mall.util.BeanUtil;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexConfigServiceImpl implements IndexConfigService {

    @Resource
    private IndexConfigMapper indexConfigMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<IndexConfigGoodsDto> queryIndexConfigGoodsByType(int configType, int number) {
        List<IndexConfigGoodsDto> indexConfigGoodsDtoList = new ArrayList<>(number);
        List<IndexConfig> indexConfigList = indexConfigMapper.queryIndexConfigGoodsByType(configType, number);
        if (CollectionUtils.isEmpty(indexConfigList)) {
            return indexConfigGoodsDtoList;
        }
        // 所有的goodsId
        List<Long> goodsIds = indexConfigList.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
        List<Goods> goodList = goodsMapper.queryGoodsByGoodIds(goodsIds);
        indexConfigGoodsDtoList = BeanUtil.copyList(goodList, IndexConfigGoodsDto.class);
        for (IndexConfigGoodsDto indexConfigGoodsDto : indexConfigGoodsDtoList) {
            String goodsName = indexConfigGoodsDto.getGoodsName();
            String goodsIntro = indexConfigGoodsDto.getGoodsIntro();
            // 字符串过长导致文字超出的问题
            if (goodsName.length() > 30) {
                goodsName = goodsName.substring(0, 30) + "...";
                indexConfigGoodsDto.setGoodsName(goodsName);
            }
            if (goodsIntro.length() > 22) {
                goodsIntro = goodsIntro.substring(0, 22) + "...";
                indexConfigGoodsDto.setGoodsIntro(goodsIntro);
            }
        }

        return indexConfigGoodsDtoList;
    }

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }


    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
