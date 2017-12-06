package cn.my.spring.service.impl;

import cn.my.spring.annotation.MyService;
import cn.my.spring.service.UserService;

@MyService
public class UserSereviceImpl implements UserService {

    @Override
    public void getUserInfo() {
        System.out.println("获取用户信息。。。。。。。。。");
    }
}
