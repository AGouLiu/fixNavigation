package com.agou.nav_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ActivityDestination {

    String pageUrl();

    /**
     * 是否需要登录后跳转
     */
    boolean needLogin() default false;

    /**
     * 是否为首页
     */
    boolean asStarter() default false;
}


