package com.lhr.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-18 22:12
 **/


@Controller
public class LoginController {


    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login-page");
    }

}
