
package com.ratel.shop.mall.controller.mall;

import cn.hutool.core.util.StrUtil;
import com.ratel.shop.mall.common.Constants;
import com.ratel.shop.mall.common.ServiceResultEnum;
import com.ratel.shop.mall.dto.UserDto;
import com.ratel.shop.mall.entity.User;
import com.ratel.shop.mall.service.UserService;
import com.ratel.shop.mall.util.MD5Util;
import com.ratel.shop.mall.util.Result;
import com.ratel.shop.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PersonalController {

    @Resource
    private UserService userService;

    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        request.setAttribute("path", "personal");
        return "mall/personal";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "mall/login";
    }

    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "mall/register";
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        if (StrUtil.isBlank(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StrUtil.isBlank(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StrUtil.isBlank(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StrUtil.isBlank(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = userService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        // 登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            return ResultGenerator.genSuccessResult();
        }
        // 登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        if (StrUtil.isBlank(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StrUtil.isBlank(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StrUtil.isBlank(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StrUtil.isBlank(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String registerResult = userService.register(loginName, password);
        // 注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        // 注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PostMapping("/personal/update")
    @ResponseBody
    public Result update(@RequestBody User user, HttpSession httpSession) {
        UserDto userDto = userService.update(user, httpSession);
        Result result;
        if (userDto == null) {
            result = ResultGenerator.genFailResult("修改失败");
            return result;
        } else {
            result = ResultGenerator.genSuccessResult();
        }
        return result;
    }
}
