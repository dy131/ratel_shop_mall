
package com.ratel.shop.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ratel.shop.mall.entity.User;
import com.ratel.shop.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    User queryUserByLoginNameAndPassword(@Param("loginName") String loginName, @Param("password") String password);

    User queryUserByLoginName(String loginName);

    User queryUserByUserId(Long userId);

    int updateUserByUserId(User user);


    int deleteByPrimaryKey(Long userId);







    int updateByPrimaryKey(User record);

    List<User> findMallUserList(PageQueryUtil pageUtil);

    int getTotalMallUsers(PageQueryUtil pageUtil);

    int lockUserBatch(@Param("ids") Integer[] ids, @Param("lockStatus") int lockStatus);
}