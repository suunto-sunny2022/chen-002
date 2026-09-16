package com.chen.framework.security;

import com.chen.system.entity.User;
import com.chen.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRealm extends AuthorizingRealm {

    private final UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(user.getRole());
        if ("admin".equals(user.getRole())) {
            info.addStringPermission("*");
        } else {
            info.addStringPermission("user:read");
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        User user = userService.findByUsername(String.valueOf(token.getPrincipal()));
        if (user == null) {
            throw new UnknownAccountException();
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new DisabledAccountException();
        }
        return new SimpleAuthenticationInfo(
            user,
            user.getPassword(),
            ByteSource.Util.bytes(user.getSalt()),
            getName()
        );
    }
}
