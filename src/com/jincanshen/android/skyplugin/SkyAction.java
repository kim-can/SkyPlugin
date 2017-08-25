package com.jincanshen.android.skyplugin;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.util.PsiUtilBase;
import com.jincanshen.android.skyplugin.common.Utils;
import com.jincanshen.android.skyplugin.dialog.EntryList;
import com.jincanshen.android.skyplugin.dialog.interfaces.ICancelListener;
import com.jincanshen.android.skyplugin.dialog.interfaces.IConfirmListener;
import com.jincanshen.android.skyplugin.model.SkyElement;
import com.jincanshen.android.skyplugin.sky.ISky;
import com.jincanshen.android.skyplugin.sky.SkyFactory;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by sky on 2017/6/11.
 */
public class SkyAction extends BaseGenerateAction implements IConfirmListener, ICancelListener {

	protected JFrame				mDialog;

	protected static final Logger	log	= Logger.getInstance(SkyAction.class);

	@SuppressWarnings("unused") public SkyAction() {
		super(null);
	}

	@SuppressWarnings("unused") public SkyAction(CodeInsightActionHandler handler) {
		super(handler);
	}

	@Override protected boolean isValidForClass(final PsiClass targetClass) {
		ISky iSky = SkyFactory.findSkyForPsiElement(targetClass.getProject(), targetClass);

		return (iSky != null && super.isValidForClass(targetClass) && Utils.findAndroidSDK() != null && !(targetClass instanceof PsiAnonymousClass));
	}

	@Override public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
		ISky iSky = SkyFactory.findSkyForPsiElement(project, file);
		return (iSky != null && super.isValidForFile(project, editor, file));
	}

	@Override public void actionPerformed(AnActionEvent event) {
		Project project = event.getData(PlatformDataKeys.PROJECT);
		Editor editor = event.getData(PlatformDataKeys.EDITOR);

		actionPerformedImpl(project, editor);
	}

	@Override public void actionPerformedImpl(Project project, Editor editor) {
		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		ArrayList<SkyElement> elements = Utils.getMethodFromClass(getTargetClass(editor, file));
		if (!elements.isEmpty()) {
			showDialog(project, editor, elements);
		} else {
			Utils.showErrorNotification(project, "没有找到方法~~~");
		}
	}

	protected void showDialog(Project project, Editor editor, ArrayList<SkyElement> elements) {
		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		if (file == null) {
			return;
		}
		PsiClass clazz = getTargetClass(editor, file);

		ISky iSky = SkyFactory.findSkyForPsiElement(project, file);
		if (clazz == null || iSky == null) {
			return;
		}
		EntryList panel = new EntryList(project, editor, elements, this, this);

		mDialog = new JFrame();
		mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
		mDialog.getContentPane().add(panel);
		mDialog.pack();
		mDialog.setLocationRelativeTo(null);
		mDialog.setVisible(true);
	}

	/**
	 * 取消
	 */
	@Override public void onCancel() {
		closeDialog();
	}

	protected void closeDialog() {
		if (mDialog == null) {
			return;
		}

		mDialog.setVisible(false);
		mDialog.dispose();
	}

	@Override public void onConfirm(Project project, Editor editor, ArrayList<SkyElement> elements, boolean isInheritor) {
		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		if (file == null) {
			return;
		}
		closeDialog();
		if (Utils.getClickCount(elements) > 0) { // generate
			new SkyCodeCreator(file,getTargetClass(editor,file),"Sky Change Method",elements,isInheritor).execute();
		} else {
			Utils.showInfoNotification(project, "No injection was selected");
		}

	}
}
