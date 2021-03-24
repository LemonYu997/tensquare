package com.tensquare.user.service;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //注入dao
    @Autowired
    private UserDao userDao;

    //根据id查询用户
    public User selectById(String userId) {
        return userDao.selectById(userId);
    }
}
