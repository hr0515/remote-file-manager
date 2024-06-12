package com.lhr.manager.config;


import com.lhr.manager.configuration.Sfe4jConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Properties.class)
@ComponentScan(
        basePackages = {"com.lhr.manager"}
)
public class AutoConfiguration {

    @Bean
    public Sfe4jConfiguration initSfe4jConfiguration(Properties properties) {
        Sfe4jConfiguration configuration = new Sfe4jConfiguration();
        configuration.setTitle(properties.getTitle());
        configuration.setDescription(properties.getDescription());
        configuration.setQuickLinks(properties.getQuickLinks());
        configuration.setBaseDirPath(StringUtils.isNotEmpty(properties.getBaseDirPath()) ? properties.getBaseDirPath() : "/");
        configuration.setRestrictToBaseDir(properties.getRestrictToBaseDir() != null ? properties.getRestrictToBaseDir() : false);
        return configuration;
    }
}
