package com.yupi.yupicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//打上这个注解就必须登录
public @interface AuthCheck {

    /**
     * 必须有某个角色，默认空字符串无限制
     */
    String mustRole() default "";
}
