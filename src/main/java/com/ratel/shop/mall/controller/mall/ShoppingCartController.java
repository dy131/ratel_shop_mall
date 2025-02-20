
package com.ratel.shop.mall.controller.mall;

import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.ShoppingCartItemDto;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.ShoppingCartItem;
import com.ratel.shop.mall.service.ShoppingCartService;
import com.ratel.shop.mall.util.Result;
import com.ratel.shop.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<ShoppingCartItemDto> myShoppingCartItems = shoppingCartService.queryMyShoppingCartItemList(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            // 购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(ShoppingCartItemDto::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            // 总价
            for (ShoppingCartItemDto newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem,
                                                 HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        String saveResult = shoppingCartService.saveShoppingCartItem(shoppingCartItem);
        // 添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody ShoppingCartItem newBeeMallShoppingCartItem,
                                                   HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        newBeeMallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = shoppingCartService.updateShoppingCartItem(newBeeMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shoppingCartService.deleteById(newBeeMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        UserDto user = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemDto> myShoppingCartItems = shoppingCartService.queryMyShoppingCartItemList(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (ShoppingCartItemDto newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
