package com.github.wangrui027.cas.client.controller;

import lombok.extern.slf4j.Slf4j;
import net.unicon.cas.client.configuration.CasClientConfigurationProperties;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Controller
public class TestController {

    @Resource
    private ServerProperties serverProperties;

    @Resource
    private CasClientConfigurationProperties casConfig;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        log.info("casConfig: {}", casConfig);
        return "word";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user(HttpServletRequest request) {
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        String loginName = null;
        if (assertion != null) {
            AttributePrincipal principal = assertion.getPrincipal();
            loginName = principal.getName();
            log.info("访问者: {}", loginName);
        }
        return "访问者:" + loginName;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) throws UnsupportedEncodingException {
        session.invalidate();
        String address = URLEncoder.encode(casConfig.getClientHostUrl() + serverProperties.getServlet().getContextPath() + "/user", "UTF-8");
        return "redirect:" + casConfig.getServerUrlPrefix() + "/logout?service=" + address;
    }

}
