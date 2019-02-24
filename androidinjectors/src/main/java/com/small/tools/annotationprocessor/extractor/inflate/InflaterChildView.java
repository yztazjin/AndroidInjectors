package com.small.tools.annotationprocessor.extractor.inflate;

import com.small.tools.annotationprocessor.annos.BindLayout;
import com.small.tools.annotationprocessor.annos.BindView;
import com.small.tools.annotationprocessor.annos.aar.BindLayout2;
import com.small.tools.annotationprocessor.annos.aar.BindView2;

import javax.lang.model.element.Element;

/**
 * Author: hjq
 * Date  : 2018/11/18 20:56
 * Name  : InflaterChildView
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class InflaterChildView {

    String mFiledName;
    String mId;
    boolean mIsLayout;

    public InflaterChildView(Element e) {
        mFiledName = e.getSimpleName().toString();
        BindLayout bl = e.getAnnotation(BindLayout.class);
        BindLayout2 bl2 = e.getAnnotation(BindLayout2.class);
        BindView bv = e.getAnnotation(BindView.class);
        BindView2 bv2 = e.getAnnotation(BindView2.class);

        if (bl != null || bl2 != null) {
            mIsLayout = true;
            if (bl != null) {
                mId = String.valueOf(bl.value());
            } else {
                mId = bl2.value();
            }

        } else if (bv != null || bv2 != null) {
            mIsLayout = false;
            if (bv != null) {
                mId = String.valueOf(bv.value());
            } else {
                mId = bv2.value();
            }
        } else {
            throw new IllegalArgumentException(e.getSimpleName()
                    + " element is not layout or view annotated!");
        }
    }

    private String getFieldName() {

        return mFiledName;
    }

    private String getId() {

        return mId;
    }

    public boolean isLayout() {

        return mIsLayout;
    }

    public String findView(String parent) {
        StringBuilder sb = new StringBuilder();
        sb.append("target.")
                .append(getFieldName())
                .append("=")
                .append("InjectorSupports.findViewById")
                .append("(")
                .append(parent)
                .append(",")
                .append(getId())
                .append(");");
        return sb.toString();
    }

    public String findLayout() {
        StringBuilder sb = new StringBuilder();
        sb.append("target.")
                .append(getFieldName())
                .append("=")
                .append("InjectorSupports.inflate")
                .append("(")
                .append("mContext")
                .append(",")
                .append(getId())
                .append(");");
        return sb.toString();
    }

}
