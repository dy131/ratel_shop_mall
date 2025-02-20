
package com.ratel.shop.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ratel.shop.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserMapper extends BaseMapper<AdminUser> {
    int insert(AdminUser record);

    int insertSelective(AdminUser record);

    /**
     * 登陆方法
     *
     * @param userName
     * @param password
     * @return
     */
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser selectByPrimaryKey(Integer adminUserId);

    int updateByPrimaryKeySelective(AdminUser record);

    int updateByPrimaryKey(AdminUser record);
}