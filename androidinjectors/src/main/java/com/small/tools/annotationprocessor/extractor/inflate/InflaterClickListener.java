package com.small.tools.annotationprocessor.extractor.inflate;

import com.small.tools.annotationprocessor.annos.BindClick;
import com.small.tools.annotationprocessor.annos.aar.BindClick2;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Author: hjq
 * Date  : 2019/02/23 21:54
 * Name  : InflaterClickListener
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class InflaterClickListener {
    private String mMethodName;
    private List<? extends VariableElement> mMethodParams;
    private String[] mViewIds;

    public InflaterClickListener(ExecutableElement e) {
        BindClick bl = e.getAnnotation(BindClick.class);
        BindClick2 bl2 = e.getAnnotation(BindClick2.class);

        if (bl == null
                && bl2 == null) {
            return;
        } else if (bl != null
                && bl.value() != null
                && bl.value().length > 0) {
            mViewIds = new String[bl.value().length];
            for (int i = 0; i < bl.value().length; i++) {
                mViewIds[i] = String.valueOf(bl.value()[i]);
            }
        }

        mMethodName = e.getSimpleName().toString();
        mMethodParams = e.getParameters();
    }

    private String getViewIds() {
        StringBuilder sb = new StringBuilder();
        if (mViewIds == null
                || mViewIds.length < 1) {
            sb.append("null");
            return sb.toString();
        } else {
            sb.append("new int[]{");
            for (String id : mViewIds) {
                sb.append(id).append(",");
            }
            return sb.substring(0, sb.length() - 1) + "}";
        }
    }

    private String getParams() {
        StringBuilder sb = new StringBuilder();
        if (mMethodParams == null
                || mMethodParams.size() < 1) {
            sb.append("null");
            return sb.toString();
        } else {
            sb.append("new String[]{");
            for (VariableElement element : mMethodParams) {
                sb.append("\"")
                        .append(element.asType().toString())
                        .append("\"")
                        .append(",");
            }
            return sb.substring(0, sb.length() - 1) + "}";
        }
    }

    public String setOnClickListener() {
        StringBuilder sb = new StringBuilder();
        sb.append("InjectorSupports.setOnClickListener(")
                .append(getViewIds())
                .append(", mContentView")
                .append(", target")
                .append(", \"").append(mMethodName).append("\"")
                .append(", ").append(getParams())
                .append(");");
        return sb.toString();
    }

}
