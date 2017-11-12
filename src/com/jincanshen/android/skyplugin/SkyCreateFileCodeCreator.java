package com.jincanshen.android.skyplugin;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.oracle.tools.packager.Log;

import java.util.Properties;

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

				xml = gennerateXml(xmlName);
				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYActivity");
				break;
			case 1: // fragment
				if (!viewName.endsWith("Fragment")) {
					viewName = viewName + "Fragment";
				}
				xmlName = "fragment_" + xmlName.toLowerCase();

				xml = gennerateXml(xmlName);
				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYFragment");
				break;
			case 2: // dialog fragment
				if (!viewName.endsWith("DialogFragment")) {
					viewName = viewName + "DialogFragment";
				}
				xmlName = "dialogfragment_" + xmlName.toLowerCase();

				xml = gennerateXml(xmlName);
				view = generateView(viewName, xmlName, bizName, "jc.sky.view.SKYDialogFragment");
				break;
		}

		PsiFile biz = generateBiz(bizName, viewName);

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(xml);
		styleManager.optimizeImports(xml);
		styleManager.shortenClassReferences(biz);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(biz);
		styleManager.optimizeImports(view);

//		new ReformatCodeProcessor(mProject, new PsiFile[] { view, biz, xml }, null, false).runWithoutProgress();
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

				xml = gennerateXml(xmlName);

				view = generateAdapter(viewName, xmlName);
				break;
			case 1: // adapter more
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}

				String topXml = "item_" + xmlName.toLowerCase() + "_top";
				String bottomXml = "item_" + xmlName.toLowerCase() + "_bottom";

				xml = gennerateXml(topXml);

				xmlMore = gennerateXml(bottomXml);

				view = generateAdapterMore(viewName, topXml, bottomXml);
				break;
		}

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(xml);
		if (xmlMore != null) {
			styleManager.optimizeImports(xmlMore);
		}
		styleManager.optimizeImports(view.getContainingFile());
//		new ReformatCodeProcessor(mProject, new PsiFile[] { view, xml, xmlMore }, null, false).runWithoutProgress();
	}

	private PsiFile generateAdapterMore(String viewName, String xmlTopName, String xmlBottomName) {
		StringBuilder fileName = new StringBuilder(viewName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyAdapterMoreTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("LAYOUT1", xmlTopName);
		properties.setProperty("LAYOUT2", xmlBottomName);

		try {
			PsiElement psiElement = FileTemplateUtil.createFromTemplate(template, fileName.toString(), properties, psiDirectory);
			return psiElement.getContainingFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private PsiFile generateAdapter(String viewName, String xmlName) {
		StringBuilder fileName = new StringBuilder(viewName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyAdapterTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("LAYOUT", xmlName);

		try {
			PsiElement psiElement = FileTemplateUtil.createFromTemplate(template, fileName.toString(), properties, psiDirectory);
			return psiElement.getContainingFile().getOriginalFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private PsiFile generateView(String viewName, String xmlName, String bizName, String superName) {
		StringBuilder fileName = new StringBuilder(viewName);

		StringBuilder importText = new StringBuilder();
		if (superName.endsWith("SKYActivity")) {
			importText.append("import jc.sky.SKYHelper;\n");
			importText.append("import jc.sky.display.SKYIDisplay;\n");
		}
		importText.append("import android.os.Bundle;\n").append("import ").append(superName).append(";\n").append("import jc.sky.view.SKYBuilder;\n");

		StringBuilder methodText = new StringBuilder();

		if (superName.endsWith("SKYActivity")) {
			methodText.append("\tpublic static final void intent() {\n");
			methodText.append("\t\tSKYHelper.display(SKYIDisplay.class).intent(" + viewName + ".class);\n");
			methodText.append("\t}");
		} else {
			methodText.append("\tpublic static final " + viewName + " getInstance() {\n");
			methodText.append("\t\t" + viewName + " " + viewName.toLowerCase() + " = new " + viewName + "();\n");
			methodText.append("\t\tBundle bundle = new Bundle();\n");
			methodText.append("\t\t" + viewName.toLowerCase() + ".setArguments(bundle);\n");
			methodText.append("\t\treturn " + viewName.toLowerCase() + ";\n");
			methodText.append("\t}");
		}

		StringBuilder methodDialogText = new StringBuilder();
		if (superName.endsWith("SKYDialogFragment")) {
			// ADD method
			methodDialogText.append("\n@Override protected int getSKYStyle() {\n" + "        return 0;\n" + "    }\n");
		}

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyViewTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("IMPORT", importText.toString());
		properties.setProperty("SUPPORT", superName + "<" + bizName + ">");
		properties.setProperty("METHOD_INTENT", methodText.toString());
		properties.setProperty("LAYOUT", xmlName);
		properties.setProperty("METHOD_DIALOG", methodDialogText.toString());

		try {
			PsiElement psiElement = FileTemplateUtil.createFromTemplate(template, fileName.toString(), properties, psiDirectory);
			return psiElement.getContainingFile();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	private PsiFile generateBiz(String bizName, String viewName) {
		StringBuilder fileName = new StringBuilder(bizName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyBizTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("SUPPORT", viewName);

		try {
			PsiElement psiElement = FileTemplateUtil.createFromTemplate(template, fileName.toString(), properties, psiDirectory);
			return psiElement.getContainingFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
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

	private String defaultXMLContent() {
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
