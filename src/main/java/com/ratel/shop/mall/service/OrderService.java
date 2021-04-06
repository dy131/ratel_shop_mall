
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.controller.dto.OrderDetailDto;
import com.ratel.shop.mall.controller.vo.OrderDetailVO;
import com.ratel.shop.mall.controller.vo.OrderItemVO;
import com.ratel.shop.mall.controller.vo.ShoppingCartItemVO;
import com.ratel.shop.mall.controller.vo.NewBeeMallUserVO;
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
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(NewBeeMallUserVO user, List<ShoppingCartItemVO> myShoppingCartItems);



    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    Order getNewBeeMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

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

    List<OrderItemVO> getOrderItems(Long id);
}
