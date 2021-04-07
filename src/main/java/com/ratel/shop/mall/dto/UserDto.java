
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private Long userId;

    private String nickName;

    private String loginName;

    private String introduceSign;

    private String address;

    private int shopCartItemCount;
}
