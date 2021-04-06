
package com.ratel.shop.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_ratel_carousel")
public class Carousel extends BaseEntity{
    @TableId(value = "carousel_id", type = IdType.AUTO)
    private Integer carouselId;

    private String carouselUrl;

    private String redirectUrl;

    private Integer carouselRank;

    private Byte isDeleted;

    private Integer createUser;

    private Integer updateUser;
}