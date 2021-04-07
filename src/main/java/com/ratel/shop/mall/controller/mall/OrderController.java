
package com.ratel.shop.mall.controller.mall;

import cn.hutool.core.util.StrUtil;
import com.ratel.shop.mall.common.BusinessException;
import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.OrderDetailDto;
import com.ratel.shop.mall.dto.ShoppingCartItemDto;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.Order;
import com.ratel.shop.mall.service.OrderService;
import com.ratel.shop.mall.service.ShoppingCartService;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.Result;
import com.ratel.shop.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private OrderService orderService;

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        OrderDetailDto orderDetailDto = orderService.queryOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailDto == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailDto", orderDetailDto);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (params.get("page") == null) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", orderService.queryMyOrdersPageList(pageQueryUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemDto> shoppingCartItems = shoppingCartService.queryMyShoppingCartItemList(user.getUserId());
        if (StrUtil.isBlank(user.getAddress().trim())) {
            //无收货地址
            BusinessException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(shoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            BusinessException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user, shoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = orderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = orderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order newBeeMallOrder = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order newBeeMallOrder = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
