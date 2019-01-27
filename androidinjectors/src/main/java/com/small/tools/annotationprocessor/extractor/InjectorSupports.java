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

    public static void setContentView(Activity activity, int layoutId){
        activity.setContentView(layoutId);
    }

    public static void setContentView(Activity activity, View contentView){
        activity.setContentView(contentView);
    }

    public static void setContentView(ViewGroup view, int layoutId){
        view.inflate(view.getContext(), layoutId, view);
    }

    public static <T extends View> T inflate(Context context, int id){
        LayoutInflater flater = LayoutInflater.from(context);
        return (T) flater.inflate(id, null);
    }

    /*
     * Codes Support For InflateInjector Interfaces
     */
    public static void inject(InflateInjector injector, Activity target){
        ViewGroup mContentView = new FrameLayout(target);
        injector.injectGlue(target, mContentView);
        target.setContentView(mContentView);
    }

    public static void inject(InflateInjector injector, ViewGroup target){
        injector.injectGlue(target, target);
    }

    public static ViewGroup getContentView(ViewGroup target) {
        return target;
    }

    public static ViewGroup getContentView(Activity target) {
        return (ViewGroup) target.getWindow().getDecorView();
    }
}
