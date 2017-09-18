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

	private int					selectType;

	private int					extendsIndex;

	private PsiElementFactory	mFactory;

	protected SkyCreateFileCodeCreator(Project project, PsiDirectory psiDirectory, int selectType, String className, String extendsName, int extendsIndex, String command) {
		super(project, command);
		this.mProject = project;
		this.psiDirectory = psiDirectory;
		this.className = className;
		this.extendsName = extendsName;
		this.extendsIndex = extendsIndex;
		this.selectType = selectType;
		this.mFactory = JavaPsiFacade.getElementFactory(mProject);
	}

	@Override public void run() throws Throwable {
		// 首字母转大写~
		className = className.substring(0, 1).toUpperCase() + className.substring(1);

		switch (selectType) {
			case 0: // View
				runView(className);
				break;
			case 1: // adapter
				runAdapter(className);
				break;
		}

	}

	private void runView(String className) {
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

		PsiFile biz = generateBiz(bizName, viewName);

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(biz);
		styleManager.optimizeImports(biz);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(view.getContainingFile());
		new ReformatCodeProcessor(mProject, new PsiFile[] { view, biz }, null, false).runWithoutProgress();

	}

	private void runAdapter(String className) {
		String viewName = className;
		// 生成view文件
		PsiFile view = null;
		switch (extendsIndex) {
			case 0: // adapter one
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}
				view = generateAdapter(viewName, "jc.sky.view.adapter.recycleview.SKYRVAdapter");
				break;
			case 1: // adapter more
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}
				view = generateAdapterMore(viewName, "jc.sky.view.adapter.recycleview.SKYRVAdapter");
				break;
		}

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(view.getContainingFile());
		new ReformatCodeProcessor(mProject, new PsiFile[] { view }, null, false).runWithoutProgress();
	}

	private PsiFile generateAdapterMore(String viewName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();

		// 包
		contentText.append("import android.view.LayoutInflater;\n").append("import android.view.View;\n").append("import android.view.ViewGroup;\n\n").append("import jc.sky.view.SKYActivity;\n")
				.append("import jc.sky.view.adapter.recycleview.SKYHolder;\n").append("import jc.sky.view.adapter.recycleview.SKYRVAdapter;\n\n");

		// 类
		contentText.append("public class ").append(viewName).append(" extends ").append(superName).append("<").append(viewName).append(".Model, SKYHolder> {\n\n");

		contentText.append("\tpublic static final int\tTYPE_ONE\t= 1;\n\n");

		contentText.append("\tpublic static final int\tTYPE_TWO\t= 2;\n\n");

		contentText.append("\tpublic ").append(viewName).append("(SKYActivity SKYActivity) {\n" + "\t\tsuper(SKYActivity);\n" + "\t}\n\n");

		contentText.append("\t@Override public int getCustomViewType(int position) {\n" + "\t\treturn getItem(position).type;\n" + "\t}\n\n");

		contentText.append("\t@Override public SKYHolder newViewHolder(ViewGroup viewGroup, int type) {\n" + "\t\tView view;\n" + "\t\tSKYHolder skyHolder = null;\n" + "\t\tswitch (type) {\n"
				+ "\t\t\tcase TYPE_ONE:\n" + "\t\t\t\t// 修改布局文件\n" + "\t\t\t\tview = LayoutInflater.from(viewGroup.getContext()).inflate(0, viewGroup, false);\n"
				+ "\t\t\t\tskyHolder = new TopHolder(view);\n" + "\t\t\t\tbreak;\n" + "\t\t\tcase TYPE_TWO:\n" + "\t\t\t\t// 修改布局文件\n"
				+ "\t\t\t\tview = LayoutInflater.from(viewGroup.getContext()).inflate(1, viewGroup, false);\n" + "\t\t\t\tskyHolder = new BottomHolder(view);\n" + "\t\t\t\tbreak;\n"
				+ "\t\t\tdefault:\n" + "\t\t\t\tbreak;\n" + "\t\t}\n" + "\t\treturn skyHolder;\n" + "\t}\n\n");

		contentText.append("\tpublic class TopHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic TopHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic class BottomHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic BottomHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic class Model {\n" + "\n" + "\t\tpublic int type;\n" + "\t}\n");

		contentText.append("}");

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateAdapter(String viewName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();
		// 包
		contentText.append("import android.view.LayoutInflater;\n").append("import android.view.View;\n").append("import android.view.ViewGroup;\n\n").append("import jc.sky.view.SKYActivity;\n")
				.append("import jc.sky.view.adapter.recycleview.SKYHolder;\n").append("import jc.sky.view.adapter.recycleview.SKYRVAdapter;\n\n");

		// 类
		contentText.append("public class ").append(viewName).append(" extends ").append(superName).append("<").append(viewName).append(".Model, ").append(viewName).append(".ItemHolder> {\n\n");

		contentText.append("\tpublic ").append(viewName).append("(SKYActivity SKYActivity) {\n" + "\t\tsuper(SKYActivity);\n" + "\t}\n\n");

		contentText.append("\t@Override public ItemHolder newViewHolder(ViewGroup viewGroup, int type) {\n" + "\t\t// 修改布局文件\n"
				+ "\t\tView view = LayoutInflater.from(viewGroup.getContext()).inflate(0, viewGroup, false);\n" + "\t\tItemHolder itemHolder = new ItemHolder(view);\n" + "\t\treturn itemHolder;\n"
				+ "\t}\n\n");

		contentText.append("\tpublic class ItemHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic ItemHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic class Model {}\n");
		contentText.append("}");

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
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

		if (superName.endsWith("SKYDialogFragment")) {
			// ADD method
			contentText.append("\n    @Override protected int getSKYStyle() {\n" + "        return 0;\n" + "    }\n");
		}

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
