package com.jincanshen.android.skyplugin;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;
import com.jincanshen.android.skyplugin.common.Utils;
import com.jincanshen.android.skyplugin.model.SkyElement;
import com.jincanshen.android.skyplugin.sky.ISky;
import com.jincanshen.android.skyplugin.sky.SkyFactory;

import java.util.ArrayList;

/**
 * Created by sky on 2017/6/16.
 */
public class SkyCodeCreator extends WriteCommandAction.Simple {

	protected PsiFile				mFile;

	protected Project				mProject;

	protected PsiClass				mClass;

	protected ArrayList<SkyElement>	mElements;

	protected PsiElementFactory		mFactory;

	protected boolean				mIsInheritor;

	public SkyCodeCreator(PsiFile file, PsiClass clazz, String command, ArrayList<SkyElement> elements, boolean isInheritor) {
		super(clazz.getProject(), command);

		mFile = file;
		mProject = clazz.getProject();
		mClass = clazz;
		mElements = elements;
		mFactory = JavaPsiFacade.getElementFactory(mProject);
		mIsInheritor = isInheritor;
	}

	@Override public void run() throws Throwable {
		ISky iSky = SkyFactory.findSkyForPsiElement(mProject, mFile);
		if (iSky == null) {
			return; // Butterknife library is not available for project
		}

		if (mIsInheritor) {
			generateParent();
		}

		if (Utils.getClickCount(mElements) > 0) {
			generateMethod();
		}

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.optimizeImports(mFile);
		styleManager.shortenClassReferences(mClass);
		new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
	}

	private void generateMethod() {
		for (SkyElement skyElement : mElements) {
			if (skyElement.isClick) {
				// 注解线程
				if (skyElement.background != null && skyElement.isAddBackground) {
					PsiAnnotation background = skyElement.psiMethod.getModifierList().findAnnotation("sky.Background");
					if (background != null) {
						background.delete();
					}
					skyElement.psiMethod.getModifierList().addAnnotation(skyElement.background);
				} else {
					PsiAnnotation background = skyElement.psiMethod.getModifierList().findAnnotation("sky.Background");
					if (background != null) {
						background.delete();
					}
				}
				// 注解是否重复
				if (skyElement.repeat) {
					PsiAnnotation psiAnnotation = skyElement.psiMethod.getModifierList().findAnnotation("sky.Repeat");
					if (psiAnnotation != null) {
						psiAnnotation.delete();
					}
					skyElement.psiMethod.getModifierList().addAnnotation(skyElement.repeatS);
				} else {
					PsiAnnotation psiAnnotation = skyElement.psiMethod.getModifierList().findAnnotation("sky.Repeat");
					if (psiAnnotation != null) {
						psiAnnotation.delete();
					}
				}
				// 名称
				if (skyElement.methodName == null || skyElement.methodName.length() < 1) {
					continue;
				}
				if (!skyElement.methodName.equals(skyElement.psiMethod.getName())) {
					skyElement.psiMethod.setName(skyElement.methodName);
				}
			}
		}
	}

	private void generateParent() {
		PsiClass skyBiz = JavaPsiFacade.getInstance(mProject).findClass("sky.core.SKYBiz", new EverythingGlobalScope(mProject));
		if (skyBiz != null && !mClass.isInheritor(skyBiz, true)) {
			makeClassImplementParcelable(mFactory);
		}
	}

	private void makeClassImplementParcelable(PsiElementFactory elementFactory) {
		final String implementsType = "sky.core.SKYBiz";
		PsiJavaCodeReferenceElement implementsReference = elementFactory.createReferenceFromText(implementsType, mClass);
		mClass.getExtendsList().add(implementsReference);
	}
}
