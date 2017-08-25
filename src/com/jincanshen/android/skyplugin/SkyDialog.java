package com.jincanshen.android.skyplugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.event.*;

public class SkyDialog extends JDialog {

    private JPanel contentPane;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JRadioButton activityRadioButton;

    private JRadioButton fragmentRadioButton;

    private JRadioButton dialogFragmentRadioButton;
    private JPanel mPanel;
    private JTextField mPrefix;
    private JTextField mHolderName;

    private ButtonGroup buttonGroup;

    public SkyDialog(Project project, PsiClass targetClass, PsiElementFactory factory, PsiFile... files) {
        setContentPane(contentPane);
        setModal(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        SkyDialog dialog = new SkyDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
