package com.lhr.manager.config;


import com.lhr.manager.configuration.Sfe4jConfiguration;
import com.lhr.manager.service.FileExplorerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public FileExplorerService initFileExplorerService(Sfe4jConfiguration fileExplorerProperties) {
        FileExplorerService fileExplorerService = new FileExplorerService();
        fileExplorerService.setSfe4jConfiguration(fileExplorerProperties);
        return fileExplorerService;
    }
}
