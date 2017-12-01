package com.jincanshen.android.skyplugin.common;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.jincanshen.android.skyplugin.model.SkyElement;
import com.intellij.openapi.ui.MessageType;

import java.util.ArrayList;

import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {

	private static final Logger log = Logger.getInstance(Utils.class);

	/**
	 * Is using Android SDK?
	 */
	public static Sdk findAndroidSDK() {
		Sdk[] allJDKs = ProjectJdkTable.getInstance().getAllJdks();
		for (Sdk sdk : allJDKs) {
			if (sdk.getSdkType().getName().toLowerCase().contains("android")) {
				return sdk;
			}
		}

		return null; // no Android SDK found
	}

	public static SkyElement getMethodFromClass(Project project, Editor editor, PsiClass targetClass) {
		PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
		int offset = editor.getCaretModel().getOffset();
		PsiElement candidateA = file.findElementAt(offset);
		PsiElement candidateB = file.findElementAt(offset - 1);

		PsiMethod[] psiMethods = targetClass.getMethods();

		SkyElement skyElement = new SkyElement();
		PsiMethod psiMethod = null;
		for (PsiMethod item : psiMethods) {
			if (candidateA.getText().equals(item.getName())) {
				psiMethod = item;
				break;
			}
			if (candidateB.getText().equals(item.getName())) {
				psiMethod = item;
				break;
			}
		}
		if (psiMethod == null) {
			return null;
		}
		skyElement.psiMethod = psiMethod;
		skyElement.methodName = psiMethod.getName();
		PsiAnnotation background = psiMethod.getModifierList().findAnnotation("sky.Background");

		if (background != null) {
			StringBuilder backgroundS = new StringBuilder(background.getQualifiedName());
			if (background.getParameterList().getAttributes().length > 0) {
				PsiAnnotationMemberValue psiAnnotationMemberValue = background.getParameterList().getAttributes()[0].getValue();
				backgroundS.append("(");
				backgroundS.append(psiAnnotationMemberValue.getText());
				backgroundS.append(")");
			}
			skyElement.background = backgroundS.toString();
			skyElement.isAddBackground = true;
			skyElement.isClick = false;
		} else {
			skyElement.isAddBackground = false;
			skyElement.isClick = true;
		}

		PsiAnnotation psiAnnotation = psiMethod.getModifierList().findAnnotation("sky.Repeat");
		if (psiAnnotation != null) {
			StringBuilder repeatS = new StringBuilder(psiAnnotation.getQualifiedName());
			if (psiAnnotation.getParameterList().getAttributes().length > 0) {
				PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.getParameterList().getAttributes()[0].getValue();
				repeatS.append("(");
				repeatS.append(psiAnnotationMemberValue.getText());
				repeatS.append("(");
			}
			skyElement.repeatS = repeatS.toString();
			skyElement.repeat = true;
		} else {
			skyElement.repeat = false;
		}

		return skyElement;
	}

	public static ArrayList<SkyElement> getMethodFromClass(PsiClass targetClass) {
		final ArrayList<SkyElement> elements = new ArrayList<>();

		PsiMethod[] psiMethods = targetClass.getMethods();

		for (PsiMethod psiMethod : psiMethods) {

			PsiType psiType = psiMethod.getReturnType();
			if (psiType == null || !psiType.equals(PsiType.VOID)) {
				continue;
			}

			PsiAnnotation background = psiMethod.getModifierList().findAnnotation("sky.Background");
			if (background == null) {
				PsiAnnotation psiAnnotation = psiMethod.getModifierList().findAnnotation("java.lang.Override");
				if (psiAnnotation != null) {
					continue;
				}
			}

			SkyElement skyElement = new SkyElement();
			skyElement.psiMethod = psiMethod;
			skyElement.methodName = psiMethod.getName();
			if (background != null) {
				StringBuilder backgroundS = new StringBuilder(background.getQualifiedName());
				if (background.getParameterList().getAttributes().length > 0) {
					PsiAnnotationMemberValue psiAnnotationMemberValue = background.getParameterList().getAttributes()[0].getValue();
					backgroundS.append("(");
					backgroundS.append(psiAnnotationMemberValue.getText());
					backgroundS.append(")");
				}
				skyElement.background = backgroundS.toString();
				skyElement.isAddBackground = true;
				skyElement.isClick = false;
			} else {
				skyElement.isAddBackground = false;
				skyElement.isClick = true;
			}
			PsiAnnotation psiAnnotation = psiMethod.getModifierList().findAnnotation("sky.Repeat");
			if (psiAnnotation != null) {
				StringBuilder repeatS = new StringBuilder(psiAnnotation.getQualifiedName());
				if (psiAnnotation.getParameterList().getAttributes().length > 0) {
					PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.getParameterList().getAttributes()[0].getValue();
					repeatS.append("(");
					repeatS.append(psiAnnotationMemberValue.getText());
					repeatS.append("(");
				}
				skyElement.repeatS = repeatS.toString();
				skyElement.repeat = true;
			} else {
				skyElement.repeat = false;
			}

			elements.add(skyElement);
		}

		return elements;
	}

	/**
	 * Display simple notification - information
	 *
	 * @param project
	 * @param text
	 */
	public static void showInfoNotification(Project project, String text) {
		showNotification(project, MessageType.INFO, text);
	}

	/**
	 * Display simple notification - error
	 *
	 * @param project
	 * @param text
	 */
	public static void showErrorNotification(Project project, String text) {
		showNotification(project, MessageType.ERROR, text);
	}

	/**
	 * Display simple notification of given type
	 *
	 * @param project
	 * @param type
	 * @param text
	 */
	public static void showNotification(Project project, MessageType type, String text) {
		StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

		JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, type, null).setFadeoutTime(7500).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()),
				Balloon.Position.atRight);
	}

	public static boolean isClassAvailableForPsiFile(@NotNull Project project, @NotNull PsiElement psiElement, @NotNull String className) {
		Module module = ModuleUtil.findModuleForPsiElement(psiElement);
		if (module == null) {
			return false;
		}
		GlobalSearchScope moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
		PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className, moduleScope);
		return classInModule != null;
	}

	public static boolean isClassAvailableForProject(@NotNull Project project, @NotNull String className) {
		PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className, new EverythingGlobalScope(project));
		return classInModule != null;
	}

	@Nullable public static PsiAnnotation findAnnotationOnMethod(PsiMethod psiMethod, String annotationName) {
		PsiModifierList modifierList = psiMethod.getModifierList();
		for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
			if (annotationName.equals(psiAnnotation.getQualifiedName())) {
				return psiAnnotation;
			}
		}
		return null;
	}

	public static int getClickCount(ArrayList<SkyElement> elements) {
		int cnt = 0;
		for (SkyElement element : elements) {
			if (element.isClick) {
				cnt++;
			}
		}
		return cnt;
	}
}
