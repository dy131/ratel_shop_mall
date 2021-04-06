
package com.ratel.shop.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_ratel_shopping_cart_item")
public class ShoppingCartItem extends BaseEntity {
    @TableId(value = "cart_item_id", type = IdType.AUTO)
    private Long cartItemId;

    private Long userId;

    private Long goodsId;

    private Integer goodsCount;

    private Byte isDeleted;
}