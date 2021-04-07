
package com.ratel.shop.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ratel.shop.mall.entity.Order;
import com.ratel.shop.mall.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemMapper extends BaseMapper<ShoppingCartItem> {

    List<ShoppingCartItem> queryShoppingCartItemByUserId(@Param("userId") Long userId, @Param("number") int number);

    int queryShoppingCartNumsByUserId(Long userId);

    ShoppingCartItem queryShoppingCartByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    ShoppingCartItem queryShoppingCartByCartItemId(Long cartItemId);

    int updateShoppingCartByCartItemId(ShoppingCartItem shoppingCartItem);

    int deleteBatchByCartItemId(@Param("cardItemIds") List<Long> cardItemIds);

    int deleteByPrimaryKey(Long cartItemId);







    int updateByPrimaryKey(ShoppingCartItem record);


}