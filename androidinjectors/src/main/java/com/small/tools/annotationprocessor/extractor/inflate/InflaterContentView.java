package com.small.tools.annotationprocessor.extractor.inflate;

import com.small.tools.annotationprocessor.annos.BindContent;
import com.small.tools.annotationprocessor.annos.aar.BindContent2;

import javax.lang.model.element.TypeElement;

/**
 * Author: hjq
 * Date  : 2019/01/13 13:16
 * Name  : InflaterContentView
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class InflaterContentView {

    String mId;

    public InflaterContentView(String id) {
        mId = id;
    }

    public InflaterContentView(int id) {
        mId = String.valueOf(id);
    }

    public InflaterContentView(TypeElement element) {
        BindContent bc = element.getAnnotation(BindContent.class);
        BindContent2 bc2 = element.getAnnotation(BindContent2.class);

        if (bc != null) {
            mId = String.valueOf(bc.value());
        } else if (bc2 != null) {
            mId = bc2.value();
        } else {
            mId = null;
        }
    }

    private String getId() {
        return mId;
    }

    public String getFiledName() {
        return "mContentView";
    }

    public String injectContentView() {
        StringBuilder sb = new StringBuilder();

        if (getId() != null) {
            sb.append("InjectorSupports.setContentView(mContentView,")
                    .append(mId).append(" );\n");
        }

        return sb.toString();
    }

}
