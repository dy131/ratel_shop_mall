
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.dto.ShoppingCartItemDto;
import com.ratel.shop.mall.entity.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 保存商品至购物车
     */
    String saveShoppingCartItem(ShoppingCartItem shoppingCartItem);

    /**
     * 购物车中的列表数据
     */
    List<ShoppingCartItemDto> queryMyShoppingCartItemList(Long userId);



    /**
     * 修改购物车中的属性
     *
     * @param newBeeMallShoppingCartItem
     * @return
     */
    String updateShoppingCartItem(ShoppingCartItem newBeeMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    ShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long newBeeMallShoppingCartItemId);


}
