package com.azwe.xunke.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by AZ
 */

@Controller("/user")
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }
}
