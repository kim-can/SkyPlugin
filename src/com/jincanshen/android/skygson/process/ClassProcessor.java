package com.jincanshen.android.skygson.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.jincanshen.android.skygson.entity.ClassEntity;
import com.jincanshen.android.skygson.entity.ConvertLibrary;


public class ClassProcessor {

    private PsiElementFactory factory;
    private PsiClass cls;
    private Processor processor;

    public ClassProcessor(PsiElementFactory factory, PsiClass cls) {
        this.factory = factory;
        this.cls = cls;
        processor = Processor.getProcessor(ConvertLibrary.from());
    }

    public void generate(ClassEntity classEntity, IProcessor visitor) {
        if (processor != null) processor.process(classEntity, factory, cls, visitor);
    }
}
