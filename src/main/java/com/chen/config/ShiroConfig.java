package com.chen.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public DefaultWebSecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        filter.setSecurityManager(securityManager);
        filter.setLoginUrl("/api/auth/unauthorized");
        Map<String, String> chains = new LinkedHashMap<>();
        chains.put("/api/auth/**", "anon");
        chains.put("/druid/**", "anon");
        chains.put("/error", "anon");
        chains.put("/**", "authc");
        filter.setFilterChainDefinitionMap(chains);
        return filter;
    }
}
