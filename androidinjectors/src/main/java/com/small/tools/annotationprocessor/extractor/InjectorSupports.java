package com.small.tools.annotationprocessor.extractor;

import android.widget.FrameLayout;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.small.tools.annotationprocessor.interfaces.InflateInjector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: hjq
 * Date  : 2018/11/18 20:42
 * Name  : InjectorSupports
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class InjectorSupports {

    static Method sActivityFindViewById;
    static Method sViewFindViewById;

    static {
        try {
            sActivityFindViewById = Activity.class.getMethod("findViewById", int.class);
            sViewFindViewById = View.class.getMethod("findViewById", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static <T extends View> T findViewById(Activity activity, int id) {
        if (sActivityFindViewById == null) {
            return null;
        }

        try {
            return (T) sActivityFindViewById.invoke(activity, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends View> T findViewById(View view, int id) {
        if (sViewFindViewById == null) {
            return null;
        }

        try {
            return (T) sViewFindViewById.invoke(view, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setContentView(Activity activity, int layoutId) {
        activity.setContentView(layoutId);
    }

    public static void setContentView(Activity activity, View contentView) {
        activity.setContentView(contentView);
    }

    public static void setContentView(ViewGroup view, int layoutId) {
        view.inflate(view.getContext(), layoutId, view);
    }

    public static <T extends View> T inflate(Context context, int id) {
        LayoutInflater flater = LayoutInflater.from(context);
        return (T) flater.inflate(id, null);
    }

    /*
     * Codes Support For InflateInjector Interfaces
     */
    public static void inject(InflateInjector injector, Activity target) {
        ViewGroup mContentView = new FrameLayout(target);
        injector.injectGlue(target, mContentView);
        target.setContentView(mContentView);
    }

    public static void inject(InflateInjector injector, ViewGroup target) {
        injector.injectGlue(target, target);
    }

    public static void inject(InflateInjector injector, Object obj) {

    }

    public static ViewGroup getContentView(ViewGroup target) {
        return target;
    }

    public static ViewGroup getContentView(Activity target) {
        return (ViewGroup) target.getWindow().getDecorView();
    }

    public static ViewGroup getContentView(Object obj) {
        return null;
    }

    public static void setOnClickListener(int[] ids, View contentView,
                                          final Object target, final String method, final String[] types) {
        if (ids == null
                || target == null
                || contentView == null
                || method == null) {
            return;
        }

        final Class[] paramTypes;
        if (types != null) {
            paramTypes = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                try {
                    if (types[i].equals("int")) {
                        paramTypes[i] = int.class;
                    } else if (types[i].equals("float")) {
                        paramTypes[i] = float.class;
                    } else if (types[i].equals("long")) {
                        paramTypes[i] = long.class;
                    } else if (types[i].equals("double")) {
                        paramTypes[i] = double.class;
                    } else if (types[i].equals("boolean")) {
                        paramTypes[i] = boolean.class;
                    } else {
                        paramTypes[i] = Class.forName(types[i]);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            paramTypes = null;
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reflector.QuietReflector reflector =
                        Reflector.QuietReflector.with(target).method(method, paramTypes);
                Object[] params = null;
                if (paramTypes != null) {
                    params = new Object[paramTypes.length];
                    for (int i = 0; i < params.length; i++) {
                        Class type = paramTypes[i];
                        if (int.class.equals(type)
                                || Integer.class.equals(type)
                                || long.class.equals(type)
                                || Long.class.equals(type)
                                || float.class.equals(type)
                                || Float.class.equals(type)
                                || double.class.equals(type)
                                || Double.class.equals(type)) {
                            params[i] = -1;
                        } else if (type != Object.class
                                && type.isAssignableFrom(View.class)) {
                            params[i] = v;
                        } else {
                            params[i] = null;
                        }
                    }
                }
                reflector.call(params);
            }
        };
        for (int id : ids) {
            findViewById(contentView, id)
                    .setOnClickListener(clickListener);
        }
    }


}
