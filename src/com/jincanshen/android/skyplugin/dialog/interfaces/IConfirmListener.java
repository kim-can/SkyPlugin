package com.jincanshen.android.skyplugin.dialog.interfaces;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jincanshen.android.skyplugin.model.SkyElement;

import java.util.ArrayList;

public interface IConfirmListener {

    void onConfirm(Project project, Editor editor, ArrayList<SkyElement> elements,boolean isInheritor);
}
