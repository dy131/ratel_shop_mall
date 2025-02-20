
package com.ratel.shop.mall.interceptor;

import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.mapper.ShoppingCartItemMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 购物车数量处理
 */
@Component
public class CartNumberInterceptor implements HandlerInterceptor {

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //购物车中的数量会更改，但是在这些接口中并没有对session中的数据做修改，这里统一处理一下
        if (null != request.getSession() && null != request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            //如果当前为登陆状态，就查询数据库并设置购物车中的数量值
            UserDto userDto = (UserDto) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
            //设置购物车中的数量
            userDto.setShopCartItemCount(shoppingCartItemMapper.queryShoppingCartNumsByUserId(userDto.getUserId()));
            request.getSession().setAttribute(Constants.MALL_USER_SESSION_KEY, userDto);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
