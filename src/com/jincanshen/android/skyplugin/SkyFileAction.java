package com.jincanshen.android.skyplugin;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiPackageBase;
import com.intellij.psi.util.PsiUtilBase;

public class SkyFileAction extends AnAction {
	@Override public void actionPerformed(AnActionEvent event) {

		Project project = event.getData(PlatformDataKeys.PROJECT);

		IdeView ideView = event.getData(DataKeys.IDE_VIEW);

		final PsiDirectory selected = ideView.getOrChooseDirectory();
		if (selected == null) {
			return;
		}

		SkyCreateDialog dialog = new SkyCreateDialog(project,selected);

		dialog.setSize(300, 130);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocus();
	}
}
