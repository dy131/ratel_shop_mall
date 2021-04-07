
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.dto.OrderDetailDto;
import com.ratel.shop.mall.dto.OrderItemDto;
import com.ratel.shop.mall.dto.ShoppingCartItemDto;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.Order;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;

import java.util.List;

public interface OrderService {

    /**
     * 订单详情
     */
    OrderDetailDto queryOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 我的订单列表
     */
    PageResult queryMyOrdersPageList(PageQueryUtil pageQueryUtil);

    /**
     * 保存订单
     */
    String saveOrder(UserDto userDto, List<ShoppingCartItemDto> shoppingCartItemDto);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewBeeMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param newBeeMallOrder
     * @return
     */
    String updateOrderInfo(Order newBeeMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);


    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    Order getNewBeeMallOrderByOrderNo(String orderNo);


    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<OrderItemDto> getOrderItems(Long id);
}
