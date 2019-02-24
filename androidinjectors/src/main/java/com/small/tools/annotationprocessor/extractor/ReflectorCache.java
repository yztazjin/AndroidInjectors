package com.small.tools.annotationprocessor.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: hjq
 * Date  : 2019/02/24 20:08
 * Name  : ReflectorCache
 * Intro : Edit By hjq
 * Version : 1.0
 */
/*public*/ class ReflectorCache {

    public static final ReflectorCache sIntance = new ReflectorCache();

    Map<Class, ClassCache> mClasses;

    private ReflectorCache() {
        mClasses = new ConcurrentHashMap<>();
    }

    public static ReflectorCache get() {
        return sIntance;
    }

    public ClassCache getClassCache(Class type) {
        if (type == null) {
            return new ClassCache();
        }

        ClassCache cache = mClasses.get(type);
        if (cache == null) {
            cache = new ClassCache();
            mClasses.put(type, cache);
        }
        return cache;
    }

    public static class ClassCache {
        private Map<Integer, Method> mMethods;
        private Map<Integer, Constructor> mConstructors;
        private Map<Integer, Field> mFields;

        private ClassCache() {
            mMethods = new ConcurrentHashMap<>();
            mConstructors = new ConcurrentHashMap<>();
            mFields = new ConcurrentHashMap<>();
        }

        public Method getMethod(String name, Class... types) {
            int signature = signature(name, types);
            return mMethods.get(signature);
        }

        public void cacheMethod(Method method, String name, Class... types) {
            int signature = signature(name, types);
            mMethods.put(signature,
                    method == null ? Empty.sEmptyMethod : method);
        }

        public Field getField(String name) {
            int signature = signature(name);
            return mFields.get(signature);
        }

        public void cacheField(Field field, String name) {
            int signature = signature(name);
            mFields.put(signature, field == null ? Empty.sEmptyField : field);
        }

        public Constructor getConstructor(Class... types) {
            StringBuilder constructorSig = new StringBuilder("");
            if (types != null) {
                for (Class type : types) {
                    constructorSig.append(type.getCanonicalName());
                }
            }
            int signature = constructorSig.toString().hashCode();
            return mConstructors.get(signature);
        }

        public void cacheConstructor(Constructor constructor, Class... types) {
            int signature = signature("", types);
            mConstructors.put(signature,
                    constructor == null ? Empty.sEmptyConstructor : constructor);
        }

        public boolean isEmpty(Member member) {
            return member == null
                    || member.equals(Empty.sEmptyMethod)
                    || member.equals(Empty.sEmptyConstructor)
                    || member.equals(Empty.sEmptyField);
        }

        private int signature(String name, Class... types) {
            StringBuilder methodSig = new StringBuilder(name);
            if (types != null) {
                for (Class type : types) {
                    methodSig.append(type.getCanonicalName());
                }
            }
            int signature = methodSig.toString().hashCode();
            return signature;
        }
    }

    private static class Empty {
        static Field sEmptyField;
        static Constructor sEmptyConstructor;
        static Method sEmptyMethod;

        Field mEmptyField;

        static {
            try {
                sEmptyField = Empty.class.getDeclaredField("mEmptyField");
                sEmptyConstructor = Empty.class.getDeclaredConstructor();
                sEmptyMethod = Empty.class.getDeclaredMethod("EmptyMethod");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        void EmptyMethod() {

        }

        Empty() {

        }
    }
}
