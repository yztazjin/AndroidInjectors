package com.small.tools.annotationprocessor;

import android.view.View;
import android.view.ViewGroup;

import com.small.tools.annotationprocessor.extractor.SimpleSymbols;
import com.small.tools.annotationprocessor.interfaces.InflateInjector;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: hjq
 * Date  : 2018/11/18 13:54
 * Name  : Injectors
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class Injectors {

    private static final ConcurrentHashMap<String, InflateInjector> mInflaterCaches;

    static {
        mInflaterCaches = new ConcurrentHashMap<>();
    }

    public static void inject(Object object) {
        if (object == null) {
            return;
        }

        InflateInjector injector = getInflateInjector(object);
        injector.inject(object);
    }

    public static View injectGlue(Object object, ViewGroup group) {
        if (object == null) {
            return null;
        }

        InflateInjector injector = getInflateInjector(object);
        return injector.injectGlue(object, group);
    }

    private static InflateInjector getInflateInjector(Object object) {
        String totalClassName = object.getClass().getCanonicalName();
        String packageName = object.getClass().getPackage().getName();
        int subLen = packageName.length() + 1;
        String aboutClassName = totalClassName.substring(subLen);
        String selfClassConvertedName = aboutClassName.replace(".", "$");

        String injectorClass = packageName + "." + selfClassConvertedName + SimpleSymbols.SUFFIX;

        InflateInjector injector = mInflaterCaches.get(injectorClass);

        if (injector == null) {
            try {
                Class clazz = Class.forName(injectorClass);
                injector = (InflateInjector) clazz.newInstance();
                mInflaterCaches.put(injectorClass, injector);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return injector;
    }

}
