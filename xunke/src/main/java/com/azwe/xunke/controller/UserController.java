package com.azwe.xunke.controller;

import com.azwe.xunke.common.CommonError;
import com.azwe.xunke.common.CommonRes;
import com.azwe.xunke.common.EmBusinessError;
import com.azwe.xunke.model.UserModel;
import com.azwe.xunke.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by AZ
 */

@Controller("/user")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonRes getUser(@RequestParam(name = "id") Integer id) {
        UserModel userModel = userService.getUser(id);
        if (userModel == null) {
            return CommonRes.create(new CommonError(EmBusinessError.NO_OBJECT_FOUND), "fail");
        }
        return CommonRes.create(userModel);
    }
}
