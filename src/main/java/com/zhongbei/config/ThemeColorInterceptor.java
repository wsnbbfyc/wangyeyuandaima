package com.zhongbei.config;

import com.zhongbei.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ThemeColorInterceptor implements HandlerInterceptor {
    
    @Autowired
    private SiteSettingService siteSettingService;
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && siteSettingService != null) {
            try {
                String primaryColor = siteSettingService.getValue("background_color", "#667eea");
                String backgroundImage = siteSettingService.getValue("background_image", "");
                
                modelAndView.addObject("themePrimaryColor", primaryColor);
                modelAndView.addObject("themeBackgroundImage", backgroundImage);
            } catch (Exception e) {
                // 如果获取配置失败，使用默认值
                modelAndView.addObject("themePrimaryColor", "#667eea");
                modelAndView.addObject("themeBackgroundImage", "");
            }
        } else if (modelAndView != null) {
            // 如果 service 为 null，使用默认值
            modelAndView.addObject("themePrimaryColor", "#667eea");
            modelAndView.addObject("themeBackgroundImage", "");
        }
    }
}
