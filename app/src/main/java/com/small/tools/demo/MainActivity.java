package com.small.tools.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.small.tools.annotationprocessor.Injectors;
import com.small.tools.annotationprocessor.annos.BindContent;
import com.small.tools.annotationprocessor.annos.BindView;

@BindContent(R.layout.activity_main)
public class MainActivity extends Activity {

    @BindView(R.id.tv_hello)
    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1.
        Injectors.inject(this);
        tv_hello.setText("injectors");

        // 2.
        // DemoLayout dl = new DemoLayout(this);
        // setContentView(dl);
        // Injectors.injectGlue(this, null);
        // tv_hello.setText("injectors");

        // 3.
        // FrameLayout fm = new FrameLayout(this);
        // Injectors.injectGlue(this, fm);
        // setContentView(fm);
        // tv_hello.setText("injectors");
    }
}
