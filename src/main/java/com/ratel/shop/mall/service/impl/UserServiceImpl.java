
package com.ratel.shop.mall.service.impl;

import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.User;
import com.ratel.shop.mall.mapper.UserMapper;
import com.ratel.shop.mall.service.UserService;
import com.ratel.shop.mall.util.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        User user = userMapper.queryUserByLoginNameAndPassword(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            // 昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            UserDto userDto = new UserDto();
            BeanUtil.copyProperties(user, userDto);
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, userDto);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public UserDto update(User user, HttpSession httpSession) {
        UserDto userTemp = (UserDto) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        UserDto userDto = null;
        User user1 = userMapper.queryUserByUserId(userTemp.getUserId());
        if (user1 != null) {
            if (!StringUtils.isEmpty(user.getNickName())) {
                user1.setNickName(MallUtils.cleanString(user.getNickName()));
            }
            if (!StringUtils.isEmpty(user.getAddress())) {
                user1.setAddress(MallUtils.cleanString(user.getAddress()));
            }
            if (!StringUtils.isEmpty(user.getIntroduceSign())) {
                user1.setIntroduceSign(MallUtils.cleanString(user.getIntroduceSign()));
            }
            if (userMapper.updateUserByUserId(user1) > 0) {
                userDto = new UserDto();
                BeanUtil.copyProperties(user1, userDto);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, userDto);
            }
        }
        return userDto;
    }

    @Override
    public PageResult getNewBeeMallUsersPage(PageQueryUtil pageUtil) {
        List<User> mallUsers = userMapper.findMallUserList(pageUtil);
        int total = userMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (userMapper.queryUserByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        User user = new User();
        user.setLoginName(loginName);
        user.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        user.setPasswordMd5(passwordMD5);
        if (userMapper.insert(user) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return userMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
