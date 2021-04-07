
package com.ratel.shop.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ratel.shop.mall.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 订单列表
     */
    List<OrderItem> queryOrderItemByOrderIds(@Param("orderIds") List<Long> orderIds);

    int deleteByPrimaryKey(Long orderItemId);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long orderItemId);


    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}