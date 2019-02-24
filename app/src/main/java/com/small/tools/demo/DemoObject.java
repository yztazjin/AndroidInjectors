package com.small.tools.demo;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.small.tools.annotationprocessor.Injectors;
import com.small.tools.annotationprocessor.annos.BindContent;
import com.small.tools.annotationprocessor.annos.BindView;

/**
 * Author: hjq
 * Date  : 2019/02/23 21:38
 * Name  : DemoObject
 * Intro : Edit By hjq
 * Version : 1.0
 */
@BindContent(R.layout.activity_main)
public class DemoObject {

    @BindView(R.id.tv_hello)
    TextView tv_hello;

    public DemoObject(Context context) {
        FrameLayout content = new FrameLayout(context);
        Injectors.injectGlue(this, content);
        Log.e("test", "tv_hello "+tv_hello);
    }

}
