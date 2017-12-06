package cn.my.spring.controller;

import cn.my.spring.annotation.MyController;
import cn.my.spring.annotation.MyQuatifier;
import cn.my.spring.annotation.MyRequestMapping;
import cn.my.spring.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MyController("user")
public class UserController {

    @MyQuatifier
    private UserService userService;

    @MyRequestMapping("info")
    public String insert(HttpServletRequest request, HttpServletResponse response, String param) {
        userService.getUserInfo();
        System.out.println("调用了controller ...............");
        return null;
    }

}
