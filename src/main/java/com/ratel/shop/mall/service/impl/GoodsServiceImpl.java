
package com.ratel.shop.mall.service.impl;

import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.SearchGoodsDto;
import com.ratel.shop.mall.entity.Goods;
import com.ratel.shop.mall.mapper.GoodsMapper;
import com.ratel.shop.mall.service.GoodsService;
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
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public PageResult searchPageGoods(PageQueryUtil pageQueryUtil) {
        List<Goods> goodsList = goodsMapper.queryGoodsPageList(pageQueryUtil);
        int total = goodsMapper.queryGoodsPageCount(pageQueryUtil);
        List<SearchGoodsDto> searchGoodsDtoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(goodsList)) {

        } else {
            searchGoodsDtoList = BeanUtil.copyList(goodsList, SearchGoodsDto.class);
            for (SearchGoodsDto searchGoodsDto : searchGoodsDtoList) {
                String goodsName = searchGoodsDto.getGoodsName();
                String goodsIntro = searchGoodsDto.getGoodsIntro();
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    searchGoodsDto.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    searchGoodsDto.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(searchGoodsDtoList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        List<Goods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNewBeeMallGoods(Goods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveNewBeeMallGoods(List<Goods> newBeeMallGoodsList) {
        if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
            goodsMapper.batchInsert(newBeeMallGoodsList);
        }
    }

    @Override
    public String updateNewBeeMallGoods(Goods goods) {
        Goods temp = goodsMapper.selectById(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Goods queryGoodByGoodId(Long goodId) {
        return goodsMapper.selectById(goodId);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }


}
