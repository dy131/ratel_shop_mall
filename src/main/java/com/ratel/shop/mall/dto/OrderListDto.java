
package com.ratel.shop.mall.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单列表页面
 */
@Data
public class OrderListDto implements Serializable {

    private Long orderId;

    private String orderNo;

    private Integer totalPrice;

    private Byte payType;

    private Byte orderStatus;

    private String orderStatusString;

    private String userAddress;

    private Date createTime;

    private List<OrderItemDto> orderItemDtoList;
}
