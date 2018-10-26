package com.jincanshen.android.skyplugin.code;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sky on 2017/6/16.
 */
public class SkyCreateFileCodeCreator extends WriteCommandAction.Simple {

	private Project					mProject;

	private PsiDirectory			psiDirectory;

	private String					packageName;

	private String					className;

	private String					extendsName;

	private int						selectType;

	private int						extendsIndex;

	private PsiElementFactory		mFactory;

	protected static final Logger	log	= Logger.getInstance(SkyCreateFileCodeCreator.class);

	public SkyCreateFileCodeCreator(Project project, PsiDirectory psiDirectory, int selectType, String className, String extendsName, int extendsIndex, String command) {
		super(project, command);
		this.mProject = project;
		this.psiDirectory = psiDirectory;
		this.className = className;
		this.extendsName = extendsName;
		this.extendsIndex = extendsIndex;
		this.selectType = selectType;
		this.mFactory = JavaPsiFacade.getElementFactory(mProject);

		PsiPackage psiPackage =  JavaDirectoryService.getInstance().getPackage(psiDirectory);
		this.packageName = psiPackage.getQualifiedName();
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

	private void runView(String className) throws IOException {
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

				gennerateXml(xmlName,packageName + "." + viewName);
				view = generateView(viewName, xmlName, bizName, "sky.core.SKYActivity");
				break;
			case 1: // fragment
				if (!viewName.endsWith("Fragment")) {
					viewName = viewName + "Fragment";
				}
				xmlName = "fragment_" + xmlName.toLowerCase();

				gennerateXml(xmlName,packageName + "." + viewName);
				view = generateView(viewName, xmlName, bizName, "sky.core.SKYFragment");
				break;
			case 2: // dialog fragment
				if (!viewName.endsWith("DialogFragment")) {
					viewName = viewName + "DialogFragment";
				}
				xmlName = "dialogfragment_" + xmlName.toLowerCase();

				gennerateXml(xmlName,packageName + "." + viewName);
				view = generateView(viewName, xmlName, bizName, "sky.core.SKYDialogFragment");
				break;
		}

		PsiFile biz = generateBiz(bizName, viewName);

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(biz);
		styleManager.optimizeImports(biz);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(view);

		// new ReformatCodeProcessor(mProject, new PsiFile[] { view, biz }, null,
		// false).runWithoutProgress();
	}

	private void runAdapter(String className) throws IOException {

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

				xml = gennerateXml(xmlName,null);

				view = generateAdapter(viewName, xmlName);
				break;
			case 1: // adapter more
				if (!viewName.endsWith("Adapter")) {
					viewName = viewName + "Adapter";
				}

				String topXml = "item_" + xmlName.toLowerCase() + "_top";
				String bottomXml = "item_" + xmlName.toLowerCase() + "_bottom";

				xml = gennerateXml(topXml,null);

				xmlMore = gennerateXml(bottomXml,null);

				view = generateAdapterMore(viewName, topXml, bottomXml);
				break;
		}

		// reformat class
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
		styleManager.shortenClassReferences(view);
		styleManager.optimizeImports(view.getContainingFile());
		// new ReformatCodeProcessor(mProject, new PsiFile[] { view }, null,
		// false).runWithoutProgress();
	}

	private PsiFile generateAdapterMore(String viewName, String xmlTopName, String xmlBottomName) throws IOException {
		StringBuilder fileName = new StringBuilder(viewName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyAdapterMoreTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("LAYOUT1", xmlTopName);
		properties.setProperty("LAYOUT2", xmlBottomName);

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString() + ".java", JavaFileType.INSTANCE, template.getText(properties));
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateAdapter(String viewName, String xmlName) throws IOException {
		StringBuilder fileName = new StringBuilder(viewName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyAdapterTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("LAYOUT", xmlName);

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString() + ".java", JavaFileType.INSTANCE, template.getText(properties));
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile generateView(String viewName, String xmlName, String bizName, String superName) throws IOException {
		StringBuilder fileName = new StringBuilder(viewName);

		StringBuilder importText = new StringBuilder();
		if (superName.endsWith("SKYActivity")) {
			importText.append("import sky.core.SKYHelper;\n");
			importText.append("import sky.core.SKYIDisplay;\n");
		}
		importText.append("import android.os.Bundle;\n").append("import ").append(superName).append(";\n").append("import sky.core.SKYBuilder;\n");

		StringBuilder methodText = new StringBuilder();

		if (superName.endsWith("SKYActivity")) {
			methodText.append("public static final void intent() {\n");
			methodText.append("\t\tSKYHelper.display(SKYIDisplay.class).intent(" + viewName + ".class);\n");
			methodText.append("\t}");
		} else {
			methodText.append("public static final " + viewName + " getInstance() {\n");
			methodText.append("\t\t" + viewName + " " + viewName.toLowerCase() + " = new " + viewName + "();\n");
			methodText.append("\t\tBundle bundle = new Bundle();\n");
			methodText.append("\t\t" + viewName.toLowerCase() + ".setArguments(bundle);\n");
			methodText.append("\t\treturn " + viewName.toLowerCase() + ";\n");
			methodText.append("\t}");
		}

		StringBuilder methodDialogText = new StringBuilder();
		if (superName.endsWith("SKYDialogFragment")) {
			// ADD method
			methodDialogText.append("\t\n@Override protected int getSKYStyle() {\n" + "        return 0;\n" + "    }\n");
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

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString() + ".java", JavaFileType.INSTANCE, template.getText(properties));
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		// 注册 manifest
		if (superName.endsWith("SKYActivity")) {
			PsiFile psiFile = getManifestFile(mProject);
			if (psiFile == null) {
				return psiClass;
			}
			FileViewProvider viewProvider = psiFile.getViewProvider();
			XmlFile xmlFile = (XmlFile) viewProvider.getPsi(StdLanguages.XML);
			XmlTag xmlTag = xmlFile.getRootTag().findFirstSubTag("application");
			XmlTag tag = XmlElementFactory.getInstance(mProject)
					.createTagFromText("<activity android:name=\"" + packageName + "." + viewName + "\"\n" + "            android:screenOrientation=\"portrait\"/>");

			xmlTag.addSubTag(tag,false);

		}

		return psiClass;
	}

	private PsiFile generateBiz(String bizName, String viewName) throws IOException {
		StringBuilder fileName = new StringBuilder(bizName);

		FileTemplate template = FileTemplateManager.getInstance(mProject).getInternalTemplate("SkyBizTemplate");
		Properties defaultProperties = FileTemplateManager.getInstance(mProject).getDefaultProperties();
		Properties properties = new Properties(defaultProperties);
		properties.setProperty("NAME", fileName.toString());
		properties.setProperty("SUPPORT", viewName);

		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString() + ".java", JavaFileType.INSTANCE, template.getText(properties));
		PsiManager.getInstance(mProject).findDirectory(psiDirectory.getVirtualFile()).add(psiClass);

		return psiClass;
	}

	private PsiFile gennerateXml(String xmlName,String currentName) {

		StringBuilder fileName = new StringBuilder(xmlName);
		fileName.append(".xml");

		String contentText = defaultXMLContent(currentName);

		PsiDirectory directory = parentDirectory(psiDirectory);
		if (directory == null) {
			return null;
		}
		PsiFile psiClass = PsiFileFactory.getInstance(mProject).createFileFromText(fileName.toString(), XmlFileType.INSTANCE, contentText);
		PsiDirectory psiDirectory = PsiManager.getInstance(mProject).findDirectory(directory.getVirtualFile());
		PsiFile findFile = psiDirectory.findFile(fileName.toString());
		if (findFile != null) {
			return null;
		}
		psiDirectory.add(psiClass);
		return psiClass;
	}

	public static PsiFile getManifestFile(Project project) {
		String path = project.getBasePath() + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "AndroidManifest.xml";
		VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
		if (virtualFile == null) return null;
		return PsiManager.getInstance(project).findFile(virtualFile);
	}

	private PsiDirectory parentDirectory(PsiDirectory psiDirectory) {
		if (psiDirectory.getName().equals("main") && psiDirectory.getParentDirectory().getName().equals("src")) {
			PsiDirectory directory = psiDirectory.findSubdirectory("res");
			if (directory == null) {
				return null;
			}
			return directory.findSubdirectory("layout");
		}
		if (psiDirectory.getName().equals(mProject.getName())) {
			return null;
		}

		return parentDirectory(psiDirectory.getParentDirectory());
	}

	private String defaultXMLContent(String currentName) {
		StringBuilder contentText = new StringBuilder();
		contentText.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		contentText.append("<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
		if(currentName != null) {
			contentText.append("              xmlns:tools=\"http://schemas.android.com/tools\"\n");
		}
		contentText.append("              android:orientation=\"vertical\"\n");
		contentText.append("              android:gravity=\"center\"\n");
		contentText.append("              android:layout_width=\"match_parent\"\n");
		contentText.append("              android:layout_height=\"match_parent\"\n");
		if(currentName != null){
			contentText.append("              tools:context=\""+currentName+"\">\n");
		}
		contentText.append("\n");
		contentText.append("</RelativeLayout>");
		return contentText.toString();
	}
}
