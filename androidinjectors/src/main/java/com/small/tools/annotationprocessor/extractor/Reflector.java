package com.small.tools.annotationprocessor.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: hjq
 * Date  : 2019/02/23 23:11
 * Name  : Reflector
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class Reflector {

    protected Class mType;
    protected Method mMethod;
    protected Field mField;
    protected Constructor mConstructor;
    protected Object mCaller;

    protected ReflectorCache.ClassCache mCache;

    public static class ReflectorException extends Exception {

        protected ReflectorException(String message) {
            super(message);
        }

        protected ReflectorException(Throwable throwable) {
            super(throwable);
        }

        protected ReflectorException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }

    protected Reflector() {

    }

    public static Reflector on(Class clazz) {
        Reflector reflector = new Reflector();
        reflector.mType = clazz;
        reflector.mCache = ReflectorCache.get().getClassCache(clazz);
        return reflector;
    }

    public static Reflector on(String name, ClassLoader classLoader)
            throws ReflectorException {
        try {
            Class clazz = classLoader.loadClass(name);
            return on(clazz);
        } catch (ClassNotFoundException e) {
            throw new ReflectorException(e);
        }
    }

    public static Reflector on(String name) throws ReflectorException {
        return on(name, Reflector.class.getClassLoader());
    }

    public static Reflector with(Object object) throws ReflectorException {
        if (object == null) {
            throw new ReflectorException("param is null");
        }
        Reflector reflector = on(object.getClass());
        reflector.bind(object);
        return reflector;
    }

    public Reflector bind(Object caller) throws ReflectorException {
        mCaller = check(caller);
        return this;
    }

    public Reflector unbind() {
        mCaller = null;
        return this;
    }

    protected Object check(Object caller) throws ReflectorException {
        if (caller == null
                || mType.isInstance(caller)) {
            return caller;
        }
        throw new ReflectorException("caller is not instance of " + mType);
    }

    protected void check(Object caller, Member member) throws ReflectorException {
        if (member == null) {
            throw new ReflectorException("member was null!");
        }
        if (caller == null && !Modifier.isStatic(member.getModifiers())) {
            throw new ReflectorException("Need a caller!");
        }
        check(caller);
    }

    public Reflector constructor(Class<?>... types) throws ReflectorException {
        try {
            mConstructor = mCache.getConstructor(types);
            if (mConstructor == null) {
                mConstructor = mType.getDeclaredConstructor(types);
                mConstructor.setAccessible(true);
                mField = null;
                mMethod = null;
                mCache.cacheConstructor(mConstructor, types);
            } else if (mCache.isEmpty(mConstructor)) {
                throw new ReflectorException("no such constructor");
            }
            return this;
        } catch (Throwable e) {
            throw new ReflectorException(e);
        }
    }

    public <T> T newInstance(Object... args) throws ReflectorException {
        if (mConstructor == null) {
            throw new ReflectorException("constructor was null!");
        }
        try {
            return (T) mConstructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new ReflectorException(e);
        } catch (Throwable e) {
            throw new ReflectorException(e);
        }
    }

    public Reflector method(String name, Class... types) throws ReflectorException {
        mMethod = mCache.getMethod(name, types);
        if (mMethod == null) {
            mMethod = findMethod(name, types);
            mMethod.setAccessible(true);
            mField = null;
            mConstructor = null;
            mCache.cacheMethod(mMethod, name, types);
        } else if (mCache.isEmpty(mMethod)) {
            throw new ReflectorException("no such method " + name);
        }
        return this;
    }

    protected Method findMethod(String name, Class... types) throws ReflectorException {
        try {
            return mType.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            for (Class tmp = mType; tmp != null; tmp = mType.getSuperclass()) {
                try {
                    return tmp.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException e1) {

                }
            }
            throw new ReflectorException(e);
        }
    }

    public <T> T call(Object... args) throws ReflectorException {
        return call(mCaller, args);
    }

    public <T> T call(Object caller, Object... args) throws ReflectorException {
        try {
            check(caller, mMethod);
            return (T) mMethod.invoke(caller, args);
        } catch (IllegalAccessException e) {
            throw new ReflectorException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectorException(e);
        }
    }

    public Reflector field(String name) throws ReflectorException {
        mField = mCache.getField(name);
        if (mField == null) {
            mField = findField(name);
            mField.setAccessible(true);
            mMethod = null;
            mConstructor = null;
            mCache.cacheField(mField, name);
        } else if (mCache.isEmpty(mField)) {
            new ReflectorException("no such field " + name);
        }
        return this;
    }

    protected Field findField(String name) throws ReflectorException {
        try {
            return mType.getField(name);
        } catch (NoSuchFieldException e) {
            for (Class tmp = mType; tmp != null; tmp = mType.getSuperclass()) {
                try {
                    return mType.getField(name);
                } catch (NoSuchFieldException e1) {
                }
            }
            throw new ReflectorException(e);
        }
    }

    public <T> T get() throws ReflectorException {
        return get(mCaller);
    }

    public <T> T get(Object caller) throws ReflectorException {
        try {
            check(caller, mMethod);
            return (T) mField.get(caller);
        } catch (IllegalAccessException e) {
            throw new ReflectorException(e);
        }
    }

    public Reflector set(Object value) throws ReflectorException {
        return set(mCaller, value);
    }

    public Reflector set(Object caller, Object value) throws ReflectorException {
        try {
            check(caller, mMethod);
            mField.set(caller, value);
            return this;
        } catch (IllegalAccessException e) {
            throw new ReflectorException(e);
        }
    }

    public static class QuietReflector extends Reflector {

        private Throwable mIgnored;

        protected QuietReflector() {
            super();
        }

        public static QuietReflector on(Class clazz) {
            return on(clazz, null);
        }

        public static QuietReflector on(String name, ClassLoader classLoader) {
            try {
                Class clazz = classLoader.loadClass(name);
                return on(clazz);
            } catch (ClassNotFoundException e) {
                return on(null, e);
            }

        }

        public static QuietReflector on(String name) {
            return on(name, Reflector.class.getClassLoader());
        }

        public static QuietReflector with(Object object) {
            if (object == null) {
                return on(null, new ReflectorException("caller is null"));
            }
            QuietReflector reflector = on(object.getClass());
            try {
                reflector.bind(object);
            } catch (ReflectorException e) {
                reflector.mIgnored = e;
            }
            return reflector;
        }

        private static QuietReflector on(Class<?> type, Throwable ignored) {
            QuietReflector reflector = new QuietReflector();
            reflector.mType = type;
            reflector.mCache = ReflectorCache.get().getClassCache(type);
            reflector.mIgnored = ignored;
            return reflector;
        }

        public boolean ignore() {
            return alwaysIgnore() || mIgnored != null;
        }

        public boolean alwaysIgnore() {
            return mType == null;
        }

        @Override
        public QuietReflector constructor(Class<?>... types) {
            if (ignore()) {
                return this;
            }
            try {
                return (QuietReflector) super.constructor(types);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return this;
        }

        @Override
        public <T> T newInstance(Object... args) {
            if (ignore()) {
                return null;
            }
            try {
                return super.newInstance(args);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return null;
        }

        @Override
        public <T> T get() {
            return get(mCaller);
        }

        @Override
        public <T> T get(Object caller) {
            if (ignore()) {
                return null;
            }

            try {
                return super.get(caller);
            } catch (ReflectorException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public QuietReflector set(Object value) {
            return set(mCaller, value);
        }

        @Override
        public QuietReflector set(Object caller, Object value) {
            if (ignore()) {
                return this;
            }

            try {
                return (QuietReflector) super.set(caller, value);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return this;
        }

        @Override
        public QuietReflector method(String name, Class... types) {
            if (ignore()) {
                return this;
            }
            try {
                return (QuietReflector) super.method(name, types);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return this;
        }

        @Override
        public QuietReflector field(String name) {
            if (ignore()) {
                return this;
            }
            try {
                return (QuietReflector) super.field(name);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return this;
        }

        @Override
        public <T> T call(Object... args) {
            return call(mCaller, args);
        }

        @Override
        public <T> T call(Object caller, Object... args) {
            if (ignore()) {
                return null;
            }
            try {
                return super.call(caller, args);
            } catch (ReflectorException e) {
                mIgnored = e;
            }
            return null;
        }
    }

}
