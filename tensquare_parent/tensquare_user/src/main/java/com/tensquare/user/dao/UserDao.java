package com.tensquare.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tensquare.user.pojo.User;
import org.springframework.stereotype.Repository;

//使用mybatis-plus
@Repository     //消掉service注入中的报错提示
public interface UserDao extends BaseMapper<User> {
}
