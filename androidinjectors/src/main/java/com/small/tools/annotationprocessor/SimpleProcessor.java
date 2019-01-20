package com.small.tools.annotationprocessor;

import com.small.tools.annotationprocessor.annos.BindContent;
import com.small.tools.annotationprocessor.annos.BindLayout;
import com.small.tools.annotationprocessor.annos.BindView;
import com.small.tools.annotationprocessor.annos.aar.BindContent2;
import com.small.tools.annotationprocessor.annos.aar.BindLayout2;
import com.small.tools.annotationprocessor.annos.aar.BindView2;
import com.small.tools.annotationprocessor.extractor.inflate.InflaterClass;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Author: hjq
 * Date  : 2018/11/18 13:41
 * Name  : SimpleProcessor
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class SimpleProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Util.mFiler = processingEnv.getFiler();
        Util.mElements = processingEnv.getElementUtils();
        Util.mPrinter = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element e : roundEnvironment.getElementsAnnotatedWith(BindContent.class)) {
            InflaterClass clazz = new InflaterClass((TypeElement) e);
            Util.generateClass(clazz);
        }

        for (Element e : roundEnvironment.getElementsAnnotatedWith(BindContent2.class)) {

        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(BindLayout.class.getCanonicalName());
                add(BindContent.class.getCanonicalName());
                add(BindView.class.getCanonicalName());

                add(BindLayout2.class.getCanonicalName());
                add(BindContent2.class.getCanonicalName());
                add(BindView2.class.getCanonicalName());
            }
        };

    }


    public static class Util {

        static Filer mFiler;
        static Elements mElements;
        static Messager mPrinter;

        public static void log(String message) {
            if (mPrinter == null) {
                return;
            }
        }

        public static String getPackageName(TypeElement element) {
            return mElements.getPackageOf(element).getQualifiedName().toString();
        }

        public static String getSimpleName(TypeElement element) {
            String packageName = getPackageName(element);
            return element.getQualifiedName().toString()
                    .substring(packageName.length() + 1)
                    .replace(".", "$");
        }

        public static void generateClass(InflaterClass clazz) {
            try {
                JavaFileObject codeFile = mFiler
                        .createSourceFile(clazz.getCanonicalName(),
                                clazz.getTypeElement());
                Writer writer = codeFile.openWriter();
                writer.append(clazz.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String getRClass() {

            return null;
        }
    }
}
