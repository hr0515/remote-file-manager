package com.lhr.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @description:
 * @author: LHR
 * @date: 2024-05-01 14:08
 **/
@Controller
public class TerminalController {

    private ServletContext servletContext;

    @GetMapping("/terminal")
    public ModelAndView terminal(@RequestParam(value = "dir", required = false) String dir, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("terminal");
        mv.addObject("ctx", servletContext.getContextPath());
        mv.addObject("explorer", dir == null ? "/" : "/file-explorer?dir=" + dir);
        mv.addObject("dir", dir);
        mv.addObject("webSocketAddr", protocol(request, dir));
        return mv;
    }


    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String protocol(HttpServletRequest request, String dir) {
        String param = dir == null ? "" : "?dir=" + dir;
        String ip = request.getServerName();
        boolean privateIP = isPrivateIP(ip);
        if (privateIP) {
            return ("http://" + ip + ":" + request.getServerPort() + "/ws/ssh" + param).replace("http", "ws");
        }else {
            return ("https://" + ip + ":" + request.getServerPort() + "/ws/ssh" + param).replace("https", "wss");
        }
    }

    public boolean isPrivateIP(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isSiteLocalAddress() || address.isLoopbackAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }


}
