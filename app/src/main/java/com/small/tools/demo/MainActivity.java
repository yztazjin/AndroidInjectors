package com.small.tools.demo;

import android.app.Activity;
import android.os.Bundle;
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
        Injectors.inject(this);
        tv_hello.setText("injectors");
    }
}
