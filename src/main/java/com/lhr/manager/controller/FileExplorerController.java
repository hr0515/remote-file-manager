package com.lhr.manager.controller;

import com.lhr.manager.service.FileExplorerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;

@Controller
public class FileExplorerController {

    private ServletContext servletContext;
    private FileExplorerService fileExplorerService;

    @Value("${root.path}")
    private String filePath;

    @Value("${repository.path}")
    private String repositoryPath;

    @GetMapping(value = {"/file-explorer", "/"})
    public ModelAndView exec(@RequestParam("dir") @Nullable String dir) {
        ModelAndView mv = new ModelAndView("file-explorer");
        mv.addObject("ctx", servletContext.getContextPath());
        mv.addObject("meta", fileExplorerService.buildMetaInfo());
        mv.addObject("fileTree", fileExplorerService.buildFileTree(dir));
        mv.addObject("upload", "/upload");
        mv.addObject("terminal", dir == null ? "/terminal" : "/terminal?dir=" + dir);
        mv.addObject("logPath", "file-explorer?dir=" + filePath);
        mv.addObject("share", "share-explorer");
        mv.addObject("version", "file-explorer?dir=" + repositoryPath);
        return mv;
    }

    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Autowired
    public void setFileExplorerService(FileExplorerService fileExplorerService) {
        this.fileExplorerService = fileExplorerService;
    }
}
