
package com.ratel.shop.mall.service.impl;

import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.ShoppingCartItemDto;
import com.ratel.shop.mall.entity.Goods;
import com.ratel.shop.mall.entity.ShoppingCartItem;
import com.ratel.shop.mall.mapper.GoodsMapper;
import com.ratel.shop.mall.mapper.ShoppingCartItemMapper;
import com.ratel.shop.mall.service.ShoppingCartService;
import com.ratel.shop.mall.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public String saveShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem shoppingCartItem1 = shoppingCartItemMapper.queryShoppingCartByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if (shoppingCartItem1 != null) {
            shoppingCartItem1.setGoodsCount(shoppingCartItem.getGoodsCount());
            return updateShoppingCartItem(shoppingCartItem1);
        }
        Goods goods = goodsMapper.queryGoodsByGoodId(shoppingCartItem.getGoodsId());
        if (goods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemMapper.queryShoppingCartNumsByUserId(shoppingCartItem.getUserId()) + 1;
        // 超出单个商品的最大数量
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // 超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        // 保存记录
        if (shoppingCartItemMapper.insert(shoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem shoppingCartItem1 = shoppingCartItemMapper.queryShoppingCartByCartItemId(shoppingCartItem.getCartItemId());
        if (shoppingCartItem1 == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        // 超出单个商品的最大数量
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        shoppingCartItem1.setGoodsCount(shoppingCartItem.getGoodsCount());
        shoppingCartItem1.setUpdateTime(new Date());
        // 修改记录
        if (shoppingCartItemMapper.updateShoppingCartByCartItemId(shoppingCartItem1) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<ShoppingCartItemDto> queryMyShoppingCartItemList(Long userId) {
        List<ShoppingCartItemDto> shoppingCartItemDtoList = new ArrayList<>();
        List<ShoppingCartItem> shoppingCartItemList = shoppingCartItemMapper.queryShoppingCartItemByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (CollectionUtils.isEmpty(shoppingCartItemList)) {
            return shoppingCartItemDtoList;
        }
        //查询商品信息并做数据转换
        List<Long> goodIds = shoppingCartItemList.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
        List<Goods> goods = goodsMapper.queryGoodsByGoodIds(goodIds);
        Map<Long, Goods> goodsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(goods)) {
            goodsMap = goods.stream().collect(Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        }
        for (ShoppingCartItem shoppingCartItem : shoppingCartItemList) {
            ShoppingCartItemDto shoppingCartItemDto = new ShoppingCartItemDto();
            BeanUtil.copyProperties(shoppingCartItem, shoppingCartItemDto);
            if (goodsMap.containsKey(shoppingCartItem.getGoodsId())) {
                Goods good = goodsMap.get(shoppingCartItem.getGoodsId());
                shoppingCartItemDto.setGoodsCoverImg(good.getGoodsCoverImg());
                String goodsName = good.getGoodsName();
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                }
                shoppingCartItemDto.setGoodsName(goodsName);
                shoppingCartItemDto.setSellingPrice(good.getSellingPrice());
                shoppingCartItemDtoList.add(shoppingCartItemDto);
            }
        }

        return shoppingCartItemDtoList;
    }





    @Override
    public ShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId) {
        return shoppingCartItemMapper.queryShoppingCartByCartItemId(newBeeMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long newBeeMallShoppingCartItemId) {
        //todo userId不同不能删除
        return shoppingCartItemMapper.deleteByPrimaryKey(newBeeMallShoppingCartItemId) > 0;
    }


}
