package com.jincanshen.android.skyplugin;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

public class SkyFileAction extends AnAction {
	@Override public void actionPerformed(AnActionEvent event) {

		Project project = event.getData(PlatformDataKeys.PROJECT);
		Editor editor = event.getData(PlatformDataKeys.EDITOR);
		IdeView ideView = event.getData(DataKeys.IDE_VIEW);

		final PsiDirectory selected = ideView.getOrChooseDirectory();
		if (selected == null) {
			return;
		}
		SkyCreateDialog dialog = new SkyCreateDialog(project,selected);

		dialog.setSize(300, 200);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocus();
	}
}
