package com.lhr.manager.controller;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-17 23:01
 **/
@Controller
public class VideoShowController {
    @GetMapping("/file-play")
    public ModelAndView play(@RequestParam("file") @Nullable String filePath) {
        ModelAndView mv = new ModelAndView("file-play");
        if (filePath == null) return mv;
        mv.addObject("fileName", Paths.get(filePath).getFileName());
        mv.addObject("absolutePath", "video?path=" + filePath);
        return mv;
    }

}
