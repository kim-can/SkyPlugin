package com.jincanshen.android.skyplugin.sky;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jincanshen.android.skyplugin.common.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkyFactory {

	private static ISky[] sSupportedSky = new ISky[] { new Sky3() };

	private SkyFactory() {
		// no construction
	}

	@Nullable public static ISky findSkyForPsiElement(@NotNull Project project, @NotNull PsiElement psiElement) {
		for (ISky iSky : sSupportedSky) {
			if (Utils.isClassAvailableForPsiFile(project, psiElement, iSky.getDistinctClassName())) {
				return iSky;
			}
		}
		// we haven't found any version of Sky in the module, let's
		// fallback to the whole project
		return findSkyForProject(project);
	}

	@Nullable private static ISky findSkyForProject(@NotNull Project project) {
		for (ISky iSky : sSupportedSky) {
			if (Utils.isClassAvailableForProject(project, iSky.getDistinctClassName())) {
				return iSky;
			}
		}
		return null;
	}
}
