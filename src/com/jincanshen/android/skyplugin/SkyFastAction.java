package com.jincanshen.android.skyplugin;

import a.a.a.b.F;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.jincanshen.android.skyplugin.common.Utils;
import com.jincanshen.android.skyplugin.model.SkyElement;
import com.jincanshen.android.skyplugin.sky.ISky;
import com.jincanshen.android.skyplugin.sky.SkyFactory;

/**
 * Created by sky on 2017/6/11.
 */
public class SkyFastAction extends BaseGenerateAction {

	protected SkyFastCreator		skyFastCreator;

	protected static final Logger	log	= Logger.getInstance(SkyFastAction.class);

	@SuppressWarnings("unused") public SkyFastAction() {
		super(null);
	}

	@SuppressWarnings("unused") public SkyFastAction(CodeInsightActionHandler handler) {
		super(handler);
	}

	@Override protected boolean isValidForClass(final PsiClass targetClass) {
		ISky iSky = SkyFactory.findSkyForPsiElement(targetClass.getProject(), targetClass);

		return (iSky != null && super.isValidForClass(targetClass) && Utils.findAndroidSDK() != null && !(targetClass instanceof PsiAnonymousClass));
	}

	@Override public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
		ISky iSky = SkyFactory.findSkyForPsiElement(project, file);
		int offset = editor.getCaretModel().getOffset();
		PsiElement candidateA = file.findElementAt(offset);
		PsiElement candidateB = file.findElementAt(offset - 1);

		PsiClass targetClass = getTargetClass(editor, file);
		PsiMethod[] psiMethods = targetClass.getMethods();

		boolean isMethod = false;
		for (PsiMethod psiMethod : psiMethods) {
			if (candidateA.getText().equals(psiMethod.getName())) {
				isMethod = true;
				break;
			}
			if (candidateB.getText().equals(psiMethod.getName())) {
				isMethod = true;
				break;
			}
		}

		return (iSky != null && super.isValidForFile(project, editor, file) && isMethod);
	}

	@Override public void actionPerformed(AnActionEvent event) {
		Project project = event.getData(PlatformDataKeys.PROJECT);
		Editor editor = event.getData(PlatformDataKeys.EDITOR);

		actionPerformedImpl(project, editor);
	}

	@Override public void actionPerformedImpl(Project project, Editor editor) {
		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		SkyElement skyElement = Utils.getMethodFromClass(project, editor, getTargetClass(editor, file));

		if (skyElement != null) {
			showDialog(project, editor, file, skyElement);
		} else {
			Utils.showErrorNotification(project, "没有找到方法~~~");
		}
	}

	protected void showDialog(Project project, Editor editor, PsiFile file, SkyElement skyElement) {
		skyFastCreator = new SkyFastCreator(project, editor, file, getTargetClass(editor, file), skyElement);
		skyFastCreator.setVisible(true);
	}
}
