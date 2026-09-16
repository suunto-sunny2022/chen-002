package com.chen.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chen.system.entity.User;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OcswMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
        strictInsertFill(metaObject, "operator", String.class, currentOperator());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    private String currentOperator() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getUsername();
            }
            return principal == null ? "system" : String.valueOf(principal);
        } catch (RuntimeException ignored) {
            return "system";
        }
    }
}
