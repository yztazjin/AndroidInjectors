package com.small.tools.demo;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.small.tools.annotationprocessor.Injectors;
import com.small.tools.annotationprocessor.annos.BindContent;

/**
 * Author: hjq
 * Date  : 2019/01/20 18:47
 * Name  : DemoLayout
 * Intro : Edit By hjq
 * Version : 1.0
 */
@BindContent(R.layout.activity_main)
public class DemoLayout extends FrameLayout {

    public DemoLayout(Context context) {
        super(context);
        Injectors.inject(this);
    }
}
