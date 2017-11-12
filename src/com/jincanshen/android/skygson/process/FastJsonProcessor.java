package com.jincanshen.android.skygson.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.jincanshen.android.skygson.entity.ClassEntity;

/**
 * Created by dim on 16/11/7.
 */
class FastJsonProcessor extends Processor {

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls,IProcessor visitor) {
        super.onEndProcess(classEntity, factory, cls, visitor);
    }
}
