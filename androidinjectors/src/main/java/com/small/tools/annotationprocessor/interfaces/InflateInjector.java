package com.small.tools.annotationprocessor.interfaces;

import android.view.View;
import android.view.ViewGroup;

/**
 * Author: hjq
 * Date  : 2018/11/18 20:32
 * Name  : InflateInjector
 * Intro : Edit By hjq
 * Version : 1.0
 */
public interface InflateInjector<T> {

    void inject(T target);

    View injectGlue(T target, ViewGroup mContainer);

}
