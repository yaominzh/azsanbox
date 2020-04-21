package com.azwe.xunke.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by AZ 2020-04-21
 */

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {
    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView(("/admin/admin/index"));
        return modelAndView;

    }
}
