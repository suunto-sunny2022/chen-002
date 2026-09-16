package com.chen.config;

import com.chen.framework.security.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroCredentialsConfig {

    @Bean
    public HashedCredentialsMatcher credentialsMatcher(UserRealm userRealm) {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher("SHA-256");
        matcher.setHashIterations(1024);
        matcher.setStoredCredentialsHexEncoded(true);
        userRealm.setCredentialsMatcher(matcher);
        return matcher;
    }
}
