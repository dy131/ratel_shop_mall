
package com.ratel.shop.mall.service;

import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.User;
import com.ratel.shop.mall.util.PageQueryUtil;
import com.ratel.shop.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface UserService {

    /**
     * 登录
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);


    /**
     * 修改用户信息
     */
    UserDto update(User user, HttpSession httpSession);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewBeeMallUsersPage(PageQueryUtil pageUtil);

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);



    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Integer[] ids, int lockStatus);
}
