package com.lhr.manager.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class PDFViewerController {
    private static final Logger logger = LoggerFactory.getLogger(PDFViewerController.class);
    @GetMapping("/pdf-viewer/get")
    public void downloadPdf(HttpServletResponse response, @RequestParam("file") String filePath) {
        File file = new File(filePath);
        logger.info("打开PDF文件 [{}]", file.getAbsolutePath());
        if (file.exists()) {
            byte[] data = null;
            FileInputStream input=null;
            try {
                input= new FileInputStream(file);
                data = new byte[input.available()];
                int read = input.read(data);
                if (read == -1) {
                    logger.error("读取文件失败 [{}]", file.getAbsolutePath());
                }
                response.getOutputStream().write(data);
            } catch (Exception e) {
                logger.error("pdf文件处理异常 [{}]", e.toString());
            }finally{
                try {
                    if(input!=null){
                        input.close();
                    }
                } catch (IOException e) {
                    logger.error("关闭资源失败 [{}]", e.toString());
                }
            }
        }
        else {
            logger.error("文件不存在 [{}]", file.getAbsolutePath());
        }
    }

}
