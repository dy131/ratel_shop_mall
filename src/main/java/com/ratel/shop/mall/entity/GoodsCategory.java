
package com.ratel.shop.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_ratel_goods_category")
public class GoodsCategory extends BaseEntity {
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    private Byte categoryLevel;

    private Long parentId;

    private String categoryName;

    private Integer categoryRank;

    private Byte isDeleted;

    private Integer createUser;

    private Integer updateUser;
}