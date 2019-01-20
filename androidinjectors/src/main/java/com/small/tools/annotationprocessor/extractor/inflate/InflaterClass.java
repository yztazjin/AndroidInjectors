package com.small.tools.annotationprocessor.extractor.inflate;

import com.small.tools.annotationprocessor.annos.BindLayout;
import com.small.tools.annotationprocessor.annos.BindView;
import com.small.tools.annotationprocessor.annos.aar.BindLayout2;

import static com.small.tools.annotationprocessor.extractor.SimpleSymbols.*;
import static com.small.tools.annotationprocessor.SimpleProcessor.Util;

import java.util.ArrayList;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Author: hjq
 * Date  : 2018/11/18 20:56
 * Name  : InflaterClass
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class InflaterClass {

    private static final String[] IMPORTS = new String[]{
            "import android.app.Activity;\n",
            "import android.view.View;\n",
            "import android.view.LayoutInflater;\n",
            "import android.widget.AdapterView;\n",
            "import android.content.Intent;\n",
            "import android.view.ViewGroup;\n",
            "import android.widget.FrameLayout;\n",
            "import com.small.tools.annotationprocessor.interfaces.InflateInjector;\n",
            "import android.content.Context;\n",
            "import com.small.tools.annotationprocessor.extractor.InjectorSupports;\n"

    };

    private ArrayList<InflaterChildView> mSubViews;
    private InflaterContentView mContentView;
    private TypeElement mTypeElement;

    public InflaterClass(TypeElement element) {
        mTypeElement = element;
        mSubViews = new ArrayList<>();
        initUis();
    }

    private void initUis() {
        setContentView(new InflaterContentView(mTypeElement));
        for (Element element : mTypeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                if (element.getAnnotation(BindLayout.class) != null
                        || element.getAnnotation(BindView.class) != null
                        || element.getAnnotation(BindLayout2.class) != null
                        || element.getAnnotation(BindLayout.class) != null)
                    addSubView(new InflaterChildView(element));
            }
        }
    }

    public void addSubView(InflaterChildView subView) {
        mSubViews.add(subView);
    }

    public void setContentView(InflaterContentView contentView) {
        mContentView = contentView;
    }

    public String getSimpleName() {
        return Util.getSimpleName(mTypeElement) + SUFFIX;
    }

    public String getCanonicalName() {
        return Util.getPackageName(mTypeElement) + "." + getSimpleName();
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    @Override
    public String toString() {

        StringBuilder lines = new StringBuilder();
        lines.append("package")
                .append(SPACE)
                .append(Util.getPackageName(mTypeElement))
                .append(";\n");

        for (String tmp : IMPORTS) {
            lines.append(tmp);
        }

        lines.append("public class ")
                .append(getSimpleName()).append(SPACE)
                .append("implements").append(SPACE).append("InflateInjector").append(" {\n");

        lines.append(TAB).append("public void inject(Object target) {\n");
        lines.append("ViewGroup mContentView = null;");
        lines.append("if (Activity.class.isInstance(target)){\n");
        lines.append("mContentView = new FrameLayout((Context)target);\n");
        lines.append("injectGlue(target, mContentView);\n");
        lines.append("InjectorSupports.setContentView((Activity)target, mContentView);\n")
                .append("\n} else if (ViewGroup.class.isInstance(target)){\n")
                .append("mContentView = (ViewGroup)target;\n")
                .append("injectGlue(target, mContentView);\n")
                .append("}");
        lines.append(TAB).append("}\n");

        lines.append(TAB).append("public View injectGlue(Object target, ViewGroup mContentView) {\n");
        lines.append("if (mContentView != null) {\n");
        lines.append("Context mContext = mContentView.getContext();\n");
        lines.append(mContentView.injectContentView());
        lines.append("} else {\n");
        lines.append("if (Activity.class.isInstance(target)){\n");
        lines.append("mContentView = (ViewGroup)((Activity)target).getWindow().getDecorView();\n");
        lines.append("\n} else if (ViewGroup.class.isInstance(target)){\n")
                .append("mContentView = (ViewGroup)target;\n")
                .append("}");
        lines.append("}\n");
        lines.append("findViews(target, mContentView);\n");
        lines.append("return mContentView;\n");
        lines.append("}\n");

        lines.append(TAB).append("private void findViews(Object object, View contentView) {\n");
        lines.append("Context mContext = contentView.getContext();\n");
        String strParentType = getTypeElement().getQualifiedName().toString();
        lines.append(strParentType)
                .append(" target =")
                .append("(").append(strParentType).append(")")
                .append("object;\n");
        for (InflaterChildView tmp : mSubViews) {
            if (tmp.isLayout()) {
                lines.append(tmp.findLayout());
            } else {
                lines.append(tmp.findView("contentView"));
            }
        }
        lines.append(TAB).append("}");

        lines.append("}");
        return lines.toString();
    }
}
