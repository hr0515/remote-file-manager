package com.lhr.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import com.lhr.manager.util.SQLiteHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;

/**
 * @description:
 * @author: LHR
 * @date: 2024-05-31 14:52
 **/


@Controller
public class FileShareController {

    private ServletContext servletContext;

    @Resource
    private SQLiteHelper sqLiteHelper;

    private static Logger log = LoggerFactory.getLogger(FileShareController.class);

    @GetMapping("/share-explorer")
    public ModelAndView shareListPage() {
        ModelAndView mv = new ModelAndView("share-explorer");
        mv.addObject("shares", sqLiteHelper.selectShares());
        mv.addObject("ctx", servletContext.getContextPath());
        return mv;
    }

    @GetMapping("/share")
    @ResponseBody
    public String share(@RequestParam("file") String filePath, Authentication authentication) {
        String id = null;
        User user = (User) authentication.getPrincipal();
        File file = new File(filePath);
        if (file.exists()) {
            int i = sqLiteHelper.insertShare(file.getName(), file.length(), user.getUsername(), file.getAbsolutePath(), "");
            log.info("[{}]分享文件[{}], id=[{}]", user.getUsername(), file.getAbsolutePath(), i);
            if (i != 0) {
                id = String.valueOf(i);
            }
        }
        return id;
    }

    @GetMapping("/share/clear")
    @ResponseBody
    public int clear() {
        sqLiteHelper.clearShares();
        sqLiteHelper.clearVersions();
        return 1;
    }

    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
