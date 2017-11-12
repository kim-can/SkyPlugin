package com.jincanshen.android.skygson.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.jincanshen.android.skygson.entity.ClassEntity;

public interface IProcessor {

    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass);

    void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass);
}
