package com.jincanshen.android.skybutterknife.iface;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jincanshen.android.skybutterknife.model.Element;

import java.util.ArrayList;


public interface IConfirmListener {

    void onConfirm(Project project, Editor editor, ArrayList<Element> elements, String fieldNamePrefix, boolean createHolder, boolean moduleR, boolean splitOnclickMethods);
}
