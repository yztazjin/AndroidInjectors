package com.small.tools.annotationprocessor.annos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: hjq
 * Date  : 2019/02/23 21:44
 * Name  : BindClick
 * Intro : Edit By hjq
 * Version : 1.0
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface BindClick {
    int[] value();
}
