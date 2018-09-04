package com.guangzhou.film.manageback.controller;

import com.guangzhou.film.manageback.common.Response;
import com.guangzhou.film.manageback.model.User;
import com.guangzhou.film.manageback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("save")
    public Response save(User user){
        Response response = new Response();
        user = new User();
        user.setEmail("maokai@163.com");
        user.setName("maokai");
        userService.save(user);
        return response;
    }
}
