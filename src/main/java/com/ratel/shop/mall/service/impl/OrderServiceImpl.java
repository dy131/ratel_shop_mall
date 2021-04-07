
package com.ratel.shop.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ratel.shop.mall.common.*;
import com.ratel.shop.mall.dto.*;
import com.ratel.shop.mall.entity.Goods;
import com.ratel.shop.mall.entity.Order;
import com.ratel.shop.mall.entity.OrderItem;
import com.ratel.shop.mall.entity.StockNumDTO;
import com.ratel.shop.mall.mapper.GoodsMapper;
import com.ratel.shop.mall.mapper.OrderItemMapper;
import com.ratel.shop.mall.mapper.OrderMapper;
import com.ratel.shop.mall.mapper.ShoppingCartItemMapper;
import com.ratel.shop.mall.service.OrderService;
import com.ratel.shop.mall.util.BeanUtil;
import com.ratel.shop.mall.util.NumberUtil;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public OrderDetailDto queryOrderDetailByOrderNo(String orderNo, Long userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.eq("is_deleted", 0);
        Order order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            return null;
        }
        QueryWrapper<OrderItem> queryOrderItemWrapper = new QueryWrapper<>();
        queryOrderItemWrapper.eq("order_id", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectList(queryOrderItemWrapper);
        if (!CollectionUtils.isEmpty(orderItemList)) {
            List<OrderItemDto> orderItemDtoList = BeanUtil.copyList(orderItemList, OrderItemDto.class);
            OrderDetailDto orderDetailDto = new OrderDetailDto();
            BeanUtil.copyProperties(order, orderDetailDto);
            orderDetailDto.setOrderStatusString(OrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderDetailDto.getOrderStatus()).getName());
            orderDetailDto.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailDto.getPayType()).getName());
            orderDetailDto.setOrderItemDtoList(orderItemDtoList);
            return orderDetailDto;
        }
        return null;
    }

    @Override
    public PageResult queryMyOrdersPageList(PageQueryUtil pageQueryUtil) {
        int count = orderMapper.queryTotalOrdersCount(pageQueryUtil);
        List<OrderListDto> orderListDtoList = new ArrayList<>();
        if (count > 0) {
            List<Order> orderList = orderMapper.queryTotalOrdersPageList(pageQueryUtil);
            orderListDtoList = BeanUtil.copyList(orderList, OrderListDto.class);
            for (OrderListDto orderListDto : orderListDtoList) {
                orderListDto.setOrderStatusString(OrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderListDto.getOrderStatus()).getName());
            }
            List<Long> orderIds = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderIds)) {

            } else {
                List<OrderItem> orderItemList = orderItemMapper.queryOrderItemByOrderIds(orderIds);
                Map<Long, List<OrderItem>> orderItemMaps = orderItemList.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderListDto orderListDto : orderListDtoList) {
                    if (orderItemMaps.containsKey(orderListDto.getOrderId())) {
                        List<OrderItem> orderItemMapList = orderItemMaps.get(orderListDto.getOrderId());
                        List<OrderItemDto> orderItemDtoList = BeanUtil.copyList(orderItemMapList, OrderItemDto.class);
                        orderListDto.setOrderItemDtoList(orderItemDtoList);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListDtoList, count, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String saveOrder(UserDto userDto, List<ShoppingCartItemDto> shoppingCartItemDtoList) {
        List<Long> itemIdList = shoppingCartItemDtoList.stream().map(ShoppingCartItemDto::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = shoppingCartItemDtoList.stream().map(ShoppingCartItemDto::getGoodsId).collect(Collectors.toList());
        List<Goods> goodsList = goodsMapper.queryGoodsByGoodIds(goodsIds);
        // 检查是否包含已下架商品
        List<Goods> goodsListNotSelling = goodsList.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            // 非空则表示有下架商品
            BusinessException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, Goods> goodsMap = goodsList.stream().collect(Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        // 判断商品库存
        for (ShoppingCartItemDto shoppingCartItemDto : shoppingCartItemDtoList) {
            // 查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!goodsMap.containsKey(shoppingCartItemDto.getGoodsId())) {
                BusinessException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            // 存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemDto.getGoodsCount() > goodsMap.get(shoppingCartItemDto.getGoodsId()).getStockNum()) {
                BusinessException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        // 删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goodsList)) {
            if (shoppingCartItemMapper.deleteBatchByCartItemId(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(shoppingCartItemDtoList, StockNumDTO.class);
                int updateStockNumResult = goodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    BusinessException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                // 生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                // 保存订单
                Order newBeeMallOrder = new Order();
                newBeeMallOrder.setOrderNo(orderNo);
                newBeeMallOrder.setUserId(userDto.getUserId());
                newBeeMallOrder.setUserAddress(userDto.getAddress());
                // 总价
                for (ShoppingCartItemDto newBeeMallShoppingCartItemVO : shoppingCartItemDtoList) {
                    priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    BusinessException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                newBeeMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                newBeeMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderMapper.insertSelective(newBeeMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> newBeeMallOrderItems = new ArrayList<>();
                    for (ShoppingCartItemDto newBeeMallShoppingCartItemVO : shoppingCartItemDtoList) {
                        OrderItem newBeeMallOrderItem = new OrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(newBeeMallShoppingCartItemVO, newBeeMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
                        newBeeMallOrderItems.add(newBeeMallOrderItem);
                    }
                    //保存至数据库
                    if (orderItemMapper.insertBatch(newBeeMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    BusinessException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                BusinessException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            BusinessException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        BusinessException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResult getNewBeeMallOrdersPage(PageQueryUtil pageUtil) {
        List<Order> newBeeMallOrders = orderMapper.queryTotalOrdersPageList(pageUtil);
        int total = orderMapper.queryTotalOrdersCount(pageUtil);
        PageResult pageResult = new PageResult(newBeeMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(Order newBeeMallOrder) {
        Order temp = orderMapper.selectByPrimaryKey(newBeeMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(newBeeMallOrder.getTotalPrice());
            temp.setUserAddress(newBeeMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1 && newBeeMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (orderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newBeeMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (newBeeMallOrder.getOrderStatus() == 4 || newBeeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderMapper.closeOrder(Arrays.asList(ids), OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }


    @Override
    public Order getNewBeeMallOrderByOrderNo(String orderNo) {
        return orderMapper.selectById(orderNo);
    }


    @Override
    public String cancelOrder(String orderNo, Long userId) {
        Order newBeeMallOrder = orderMapper.selectById(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (orderMapper.closeOrder(Collections.singletonList(newBeeMallOrder.getOrderId()), OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        // byOrderNo
        Order newBeeMallOrder = orderMapper.selectById(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            newBeeMallOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order newBeeMallOrder = orderMapper.selectById(orderNo);
        if (newBeeMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            newBeeMallOrder.setOrderStatus((byte) OrderStatusEnum.OREDER_PAID.getOrderStatus());
            newBeeMallOrder.setPayType((byte) payType);
            newBeeMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newBeeMallOrder.setPayTime(new Date());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long id) {
        Order newBeeMallOrder = orderMapper.selectByPrimaryKey(id);
        if (newBeeMallOrder != null) {
            // selectByOrderId
            List<OrderItem> orderItems = null;//orderItemMapper.selectById(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemDto> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemDto.class);
                return newBeeMallOrderItemVOS;
            }
        }
        return null;
    }
}