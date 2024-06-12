package com.lhr.manager.controller;

import com.lhr.manager.service.FileExplorerService;
import com.lhr.manager.vo.FileContentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;

@Controller
public class FileViewerController {

    private static final Logger logger = LoggerFactory.getLogger(FileViewerController.class);
    private FileExplorerService fileExplorerService;
    private ServletContext servletContext;

    @GetMapping("/file-viewer")
    public ModelAndView exec(@RequestParam("file") @Nullable String filePath) {
        ModelAndView mv = new ModelAndView("file-viewer");
        mv.addObject("ctx", servletContext.getContextPath());
        FileContentVO fileContentVO = fileExplorerService.readFile(filePath);
        mv.addObject("absolutePicFolderPath", fileContentVO.getAbsolutePath().replace('\\', '/'));
        mv.addObject("fileName", fileContentVO.getFileName());
        mv.addObject("fileContent", fileContentVO.getContent());
        logger.info("打开文件 [{}]", filePath);
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
