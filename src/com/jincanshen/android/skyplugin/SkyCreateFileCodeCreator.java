package com.jincanshen.android.skyplugin;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Created by sky on 2017/6/16.
 */
public class SkyCreateFileCodeCreator extends WriteCommandAction.Simple {

	private Project				mProject;

	private PsiDirectory		psiDirectory;

	private String				className;

	private String				extendsName;

	private int					extendsIndex;

	private PsiElementFactory	mFactory;

	protected SkyCreateFileCodeCreator(Project project, PsiDirectory psiDirectory, String className, String extendsName, int extendsIndex, String command) {
		super(project, command);
		this.mProject = project;
		this.psiDirectory = psiDirectory;
		this.className = className;
		this.extendsName = extendsName;
		this.extendsIndex = extendsIndex;
		this.mFactory = JavaPsiFacade.getElementFactory(mProject);
	}

	@Override public void run() throws Throwable {
		// 首字母转大写~
		className = className.substring(0, 1).toUpperCase() + className.substring(1);

		String viewName = className;

		String bizName = !className.endsWith("Biz") ? className + "Biz" : className;

		// 生成view文件
		PsiFile view = null;
		switch (extendsIndex) {
			case 0: // activity
				if (!viewName.endsWith("Activity")) {
					viewName = viewName + "Activity";
				}

				view = generateView(viewName, bizName, "jc.sky.view.SKYActivity");
				break;
			case 1: // fragment
				if (!viewName.endsWith("Fragment")) {
					viewName = viewName + "Fragment";
				}
				view = generateView(viewName, bizName, "jc.sky.view.SKYFragment");
				break;
			case 2: // dialog fragment
				if (!viewName.endsWith("DialogFragment")) {
					viewName = viewName + "DialogFragment";
				}
				view = generateView(viewName, bizName, "jc.sky.view.SKYDialogFragment");
				break;
		}

		// 生成biz文件
		PsiFile biz = generateBiz(bizName, viewName);

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(biz);
		styleManager.optimizeImports(biz);

		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(view.getContainingFile());
		new ReformatCodeProcessor(mProject, new PsiFile[] { view, biz }, null, false).runWithoutProgress();
	}

	private PsiFile generateView(String viewName, String bizName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();

		contentText.append("import android.os.Bundle;\n").append("import ").append(superName).append(";\n").append("import jc.sky.view.SKYBuilder;\n");

		contentText.append("\npublic class ").append(viewName).append(" extends ").append(superName).append("<").append(bizName).append("> {\n");

		// ADD method
		contentText.append(
				"\n    @Override protected SKYBuilder build(SKYBuilder initialSKYBuilder) {\n" + "        //initialSKYBuilder.layoutId();\n" + "        return initialSKYBuilder;\n" + "    }\n");
		// ADD method
		contentText.append("\n    @Override protected void initData(Bundle savedInstanceState) {\n" + "    } \n");

		contentText.append("}");
		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateBiz(String bizName, String viewName) {

		StringBuilder fileName = new StringBuilder(bizName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();

		contentText.append("import jc.sky.core.SKYBiz;\n").append("import android.os.Bundle;\n");
		contentText.append("\npublic class ").append(bizName).append(" extends jc.sky.core.SKYBiz<").append(viewName).append("> {\n");

		// add method
		contentText.append("\n    @Override protected void initBiz(Bundle bundle) {\n" + "        super.initBiz(bundle);\n" + "    }\n");
		contentText.append("}");

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

}
