package com.jincanshen.android.skyplugin.code;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.jincanshen.android.skyplugin.model.SkyElement;
import com.jincanshen.android.skyplugin.sky.ISky;
import com.jincanshen.android.skyplugin.sky.SkyFactory;

/**
 * Created by sky on 2017/6/16.
 */
public class SkyBackgroundCodeCreator extends WriteCommandAction.Simple {

	protected PsiFile			mFile;

	protected Project			mProject;

	protected PsiClass			mClass;

	protected SkyElement		mSkyElement;

	protected PsiElementFactory	mFactory;

	public SkyBackgroundCodeCreator(PsiFile file, PsiClass clazz, String command, SkyElement skyElement) {
		super(clazz.getProject(), command);

		mFile = file;
		mProject = clazz.getProject();
		mClass = clazz;
		mSkyElement = skyElement;
		mFactory = JavaPsiFacade.getElementFactory(mProject);
	}

	@Override public void run() throws Throwable {
		ISky iSky = SkyFactory.findSkyForPsiElement(mProject, mFile);
		if (iSky == null) {
			return;
		}

		generateMethod();

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.optimizeImports(mFile);
		styleManager.shortenClassReferences(mClass);
		new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
	}

	private void generateMethod() {
		// 注解线程
		if (mSkyElement.background != null && mSkyElement.isAddBackground) {
			PsiAnnotation background = mSkyElement.psiMethod.getModifierList().findAnnotation("sky.Background");
            if (background != null) {
                background.delete();
            }
			mSkyElement.psiMethod.getModifierList().addAnnotation(mSkyElement.background);
		}
		// 注解是否重复
		if (mSkyElement.repeat) {
			PsiAnnotation psiAnnotation = mSkyElement.psiMethod.getModifierList().findAnnotation("sky.Repeat");
			if (psiAnnotation != null) {
                psiAnnotation.delete();
            }
            
			mSkyElement.psiMethod.getModifierList().addAnnotation(mSkyElement.repeatS);
		}else {
            PsiAnnotation psiAnnotation = mSkyElement.psiMethod.getModifierList().findAnnotation("sky.Repeat");
			if (psiAnnotation != null) {
				psiAnnotation.delete();
			}
        }
	}

	private void makeClassImplementParcelable(PsiElementFactory elementFactory) {
		final String implementsType = "sky.core.SKYBiz";
		PsiJavaCodeReferenceElement implementsReference = elementFactory.createReferenceFromText(implementsType, mClass);
		mClass.getExtendsList().add(implementsReference);
	}
}
