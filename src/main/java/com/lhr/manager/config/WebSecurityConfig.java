package com.lhr.manager.config;

import com.lhr.manager.cache.UserCaching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 安全管理配置
 * @date 2022/9/26 20:53
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger loggerSecurity = LoggerFactory.getLogger(WebSecurityConfig.class);
    //配置用户信息服务
    @Bean
    public UserDetailsService userDetailsService() {
        //这里配置用户信息,这里暂时使用这种方式将用户存储在内存中
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("lhr").password("").authorities("p1").build());
        manager.createUser(User.withUsername("zyh").password("").authorities("p2").build());
        manager.createUser(User.withUsername("qgy").password("").authorities("p3").build());

        UserCaching.setUser("lhr", new com.lhr.manager.entity.User("李", "lhr", "3184020", 0, false, false));
        UserCaching.setUser("zyh", new com.lhr.manager.entity.User("赵", "zyh", "20000820", 0, false, false));
        UserCaching.setUser("qgy", new com.lhr.manager.entity.User("齐", "qgy", "3184242", 0, false, false));

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        //密码为明文方式
        return NoOpPasswordEncoder.getInstance();
//        return new BCryptPasswordEncoder();
    }

    //配置安全拦截机制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers("/unsafe/**").permitAll()
        .anyRequest().authenticated()
        .and()
        // 开启表单登录
        .formLogin()// 指定登录页面
        .loginPage("/login")
                .successHandler(new _c1())
                .failureHandler(new _c2())
        // 登录处理action
        .loginProcessingUrl("/login")
//        // 登录成功后的跳转路径, alwaysUse 为true 则登录成功后跳转此路径
//        .defaultSuccessUrl("/", true)
        // 表单登录接口放行
        .permitAll().and()// 关闭csrf
        .csrf().disable();
    }

    private static class _c1 extends SavedRequestAwareAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            // 处理成功登录的逻辑
            com.lhr.manager.entity.User user = UserCaching.getUser(request.getParameter("username"));

            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

            if (user.getLocked()) {
                response.sendRedirect("/login"); // 重定向到错误页面
            } else {

                if (savedRequest != null && savedRequest.getRedirectUrl() != null) {
                    // 如果之前有请求记录，重定向到该记录的URL
                    getRedirectStrategy().sendRedirect(request, response, savedRequest.getRedirectUrl());
                } else {
                    // 如果没有之前的请求记录，默认跳转到指定的页面
                    // super.onAuthenticationSuccess(request, response, authentication);
                    response.sendRedirect("/");
                }

                loggerSecurity.info("登录成功 {}", user.getNikeName());
                user.setTryTimes(0);
                user.setOn(true);
                UserCaching.setUser(request.getParameter("username"), user);
            }
        }
    }

    private static class _c2 implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
            // 处理登录失败的逻辑
            com.lhr.manager.entity.User user = UserCaching.getUser(request.getParameter("username"));

            if (user == null) {
                response.sendRedirect("/login"); // 重定向到错误页面
            }else {
                loggerSecurity.info("登录失败 {}", user);
                user.setTryTimes(user.getTryTimes() + 1);
                if (user.getTryTimes() >= 30) {
                    user.setLocked(true);
                }
                UserCaching.setUser(request.getParameter("username"), user);
                response.sendRedirect("/login");
            }
        }
    }

}

