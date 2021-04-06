
package com.ratel.shop.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_ratel_index_config")
public class IndexConfig extends BaseEntity {
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    private String configName;

    private Byte configType;

    private Long goodsId;

    private String redirectUrl;

    private Integer configRank;

    private Byte isDeleted;

    private Integer createUser;

    private Integer updateUser;
}