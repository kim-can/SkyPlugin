package com.jincanshen.android.skyplugin;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.oracle.tools.packager.Log;

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
		String xmlName = className;
		String viewName = className;

		String bizName = !className.endsWith("Biz") ? className + "Biz" : className;

		// 生成view文件
		PsiFile view = null;
		PsiFile xml = null;
		switch (extendsIndex) {
			case 0: // activity
				if (!viewName.endsWith("Activity")) {
					viewName = viewName + "Activity";
				}
				xmlName = "activity_" + xmlName.toLowerCase();

				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYActivity");
				xml = gennerateXml(xmlName);
				break;
			case 1: // fragment
				if (!viewName.endsWith("Fragment")) {
					viewName = viewName + "Fragment";
				}
				xmlName = "fragment_" + xmlName.toLowerCase();

				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYFragment");
				xml = gennerateXml(xmlName);
				break;
			case 2: // dialog fragment
				if (!viewName.endsWith("DialogFragment")) {
					viewName = viewName + "DialogFragment";
				}
				xmlName = "dialogfragment_" + xmlName.toLowerCase();

				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYDialogFragment");
				xml = gennerateXml(xmlName);
				break;
		}

		PsiFile biz = generateBiz(bizName, viewName);

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(biz);
		styleManager.shortenClassReferences(view);
		styleManager.shortenClassReferences(xml);
		styleManager.optimizeImports(biz);
		styleManager.optimizeImports(xml);
		styleManager.optimizeImports(view);
		styleManager.optimizeImports(view.getContainingFile());
		new ReformatCodeProcessor(mProject, new PsiFile[] { view, biz, xml }, null, false).runWithoutProgress();

	}

	private void runAdapter(String className) {

		String xmlName = className;
		String viewName = className;
		// 生成view文件
		PsiFile view = null;
		PsiFile xml = null;
		PsiFile xmlMore = null;
		switch (extendsIndex) {
			case 0: // adapter one
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}
				xmlName = "item_" + xmlName.toLowerCase() + "_item";

				view = generateAdapter(viewName, xmlName, "jc.sky.view.adapter.recycleview.SKYRVAdapter");

				xml = gennerateXml(xmlName);

				break;
			case 1: // adapter more
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}

				String topXml = "item_" + xmlName.toLowerCase() + "_top";
				String bottomXml = "item_" + xmlName.toLowerCase() + "_bottom";

				view = generateAdapterMore(viewName, topXml, bottomXml, "jc.sky.view.adapter.recycleview.SKYRVAdapter");

				xml = gennerateXml(topXml);
				xmlMore = gennerateXml(bottomXml);

				break;
		}

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(xml);
		if(xmlMore != null){
			styleManager.optimizeImports(xmlMore);
		}
		styleManager.optimizeImports(view.getContainingFile());
		new ReformatCodeProcessor(mProject, new PsiFile[] { view,xml,xmlMore }, null, false).runWithoutProgress();
	}

	private PsiFile generateAdapterMore(String viewName, String xmlTopName, String xmlBottomName, String superName) {
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
				+ "\t\t\tcase TYPE_ONE:\n" + "\t\t\t\t// 修改布局文件\n" + "\t\t\t\tview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout." + xmlTopName + ", viewGroup, false);\n"
				+ "\t\t\t\tskyHolder = new TopHolder(view);\n" + "\t\t\t\tbreak;\n" + "\t\t\tcase TYPE_TWO:\n" + "\t\t\t\t// 修改布局文件\n"
				+ "\t\t\t\tview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout." + xmlBottomName + ", viewGroup, false);\n" + "\t\t\t\tskyHolder = new BottomHolder(view);\n"
				+ "\t\t\t\tbreak;\n" + "\t\t\tdefault:\n" + "\t\t\t\tbreak;\n" + "\t\t}\n" + "\t\treturn skyHolder;\n" + "\t}\n\n");

		contentText.append("\tpublic class TopHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic TopHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic class BottomHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic BottomHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic static class Model {\n" + "\n" + "\t\tpublic int type;\n" + "\t}\n");

		contentText.append("}");

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateAdapter(String viewName, String xmlName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();
		// 包
		contentText.append("import android.view.LayoutInflater;\n").append("import android.view.View;\n").append("import android.view.ViewGroup;\n\n").append("import jc.sky.view.SKYActivity;\n")
				.append("import jc.sky.view.adapter.recycleview.SKYHolder;\n").append("import jc.sky.view.adapter.recycleview.SKYRVAdapter;\n\n");

		// 类
		contentText.append("public class ").append(viewName).append(" extends ").append(superName).append("<").append(viewName).append(".Model, ").append(viewName).append(".ItemHolder> {\n\n");

		contentText.append("\tpublic ").append(viewName).append("(SKYActivity SKYActivity) {\n" + "\t\tsuper(SKYActivity);\n" + "\t}\n\n");

		contentText.append(
				"\t@Override public ItemHolder newViewHolder(ViewGroup viewGroup, int type) {\n" + "\t\t// 修改布局文件\n" + "\t\tView view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout."
						+ xmlName + ", viewGroup, false);\n" + "\t\tItemHolder itemHolder = new ItemHolder(view);\n" + "\t\treturn itemHolder;\n" + "\t}\n\n");

		contentText.append("\tpublic class ItemHolder extends SKYHolder<Model> {\n" + "\n" + "\t\tpublic ItemHolder(View itemView) {\n" + "\t\t\tsuper(itemView);\n" + "\t\t}\n" + "\n"
				+ "\t\t@Override public void bindData(Model model, int position) {\n" + "\n" + "\t\t}\n" + "\t}\n\n");

		contentText.append("\tpublic static class Model {}\n");
		contentText.append("}");

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), JavaFileType.INSTANCE, contentText.toString());
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateView(String viewName, String xmlName, String bizName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);
		fileName.append(".java");

		StringBuilder contentText = new StringBuilder();
		if(superName.endsWith("SKYActivity")) {
			contentText.append("import jc.sky.SKYHelper;\n");
			contentText.append("import jc.sky.display.SKYIDisplay;\n");
		}
		contentText.append("import android.os.Bundle;\n").append("import ").append(superName).append(";\n").append("import jc.sky.view.SKYBuilder;\n");

		contentText.append("\npublic class ").append(viewName).append(" extends ").append(superName).append("<").append(bizName).append("> {\n");

		if(superName.endsWith("SKYActivity")){
			contentText.append("\n\tpublic static final void intent() {\n");
			contentText.append("\t\tSKYHelper.display(SKYIDisplay.class).intent("+viewName+".class);\n");
			contentText.append("\t}\n");
		}else {
			contentText.append("\n\tpublic static final "+viewName+" getInstance() {\n");
			contentText.append("\t\t"+viewName+" "+viewName.toLowerCase() +" = new "+viewName+"();\n");
			contentText.append("\t\tBundle bundle = new Bundle();\n");
			contentText.append("\t\t"+viewName.toLowerCase()+".setArguments(bundle);\n");
			contentText.append("\t\treturn "+viewName.toLowerCase()+";\n");
			contentText.append("\t}\n");
		}

		// ADD method
		contentText.append("\n    @Override protected SKYBuilder build(SKYBuilder initialSKYBuilder) {\n" + "        initialSKYBuilder.layoutId(R.layout." + xmlName + ");\n"
				+ "        return initialSKYBuilder;\n" + "    }\n");
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

	private PsiFile gennerateXml(String xmlName) {

		StringBuilder fileName = new StringBuilder(xmlName);
		fileName.append(".xml");

		String contentText = defaultXMLContent();

		String[] modulePaths = psiDirectory.getVirtualFile().toString().split("main/");
		if (modulePaths == null || modulePaths.length < 2) {
			Log.info("拆分文件时，main/ 没有匹配上");
		}

		String filePath = modulePaths[0] + "main/res/layout";

		VirtualFile directory = LocalFileSystem.getInstance().findFileByPath(filePath.replace("file://", ""));

		if (directory == null) {
			Log.info("dir is null " + filePath);
		}

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), XmlFileType.INSTANCE, contentText);
		PsiManager.getInstance(mProject).findDirectory(directory).add(psiClass);
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

	private String defaultXMLContent(){
		StringBuilder contentText = new StringBuilder();
		contentText.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		contentText.append("<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
		contentText.append("              android:orientation=\"vertical\"\n");
		contentText.append("              android:gravity=\"center\"\n");
		contentText.append("              android:layout_width=\"match_parent\"\n");
		contentText.append("              android:layout_height=\"match_parent\">\n");
		contentText.append("\n");
		contentText.append("</RelativeLayout>");
		return contentText.toString();
	}
}
