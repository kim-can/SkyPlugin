package com.jincanshen.android.skyplugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.jincanshen.android.skyplugin.code.SkyBackgroundCodeCreator;
import com.jincanshen.android.skyplugin.model.SkyElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SkyFastCreator extends JDialog {

	private JPanel			contentPane;

	private JPanel			panel_method;

	private JCheckBox		repeatCheckBox;

	private JLabel			label_mothed;

	private JRadioButton	HTTPRadioButton;

	private JRadioButton	WORKRadioButton;

	private JRadioButton	SINGLEWORKRadioButton;

	ButtonGroup				buttonGroup;

	Project					project;

	Editor					editor;

	SkyElement				mSkyElement;

	public SkyFastCreator(Project project, Editor editor, PsiFile file, PsiClass clazz, SkyElement skyElement) {
		setContentPane(contentPane);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(300, 170));
		setMaximumSize(new Dimension(300, 170));
		setPreferredSize(new Dimension(300, 170));

		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		label_mothed.setFont(new Font(label_mothed.getFont().getFontName(), Font.BOLD, label_mothed.getFont().getSize()));

		buttonGroup = new ButtonGroup();
		buttonGroup.add(HTTPRadioButton);
		buttonGroup.add(WORKRadioButton);
		buttonGroup.add(SINGLEWORKRadioButton);

		repeatCheckBox.addChangeListener(new RepeatListener());

		this.mSkyElement = skyElement;
		this.project = project;
		this.editor = editor;
		String name;
		if (mSkyElement.methodName.length() > 5) {
			name = mSkyElement.methodName.substring(0, 5) + "...";
		} else {
			int count = 5 - mSkyElement.methodName.length();
			String kg = "";
			for (int i = 0; i < count; i++) {
				kg += " ";
			}
			name = mSkyElement.methodName + kg;
		}

		label_mothed.setText(name);

		repeatCheckBox.setSelected(mSkyElement.repeat);

		if (mSkyElement.isAddBackground && mSkyElement.background != null) {
			if (mSkyElement.background.indexOf("HTTP") != -1) {
				HTTPRadioButton.setSelected(true);
			} else if (mSkyElement.background.indexOf("SINGLEWORK") != -1) {
				SINGLEWORKRadioButton.setSelected(true);
			} else if (mSkyElement.background.indexOf("WORK") != -1) {
				WORKRadioButton.setSelected(true);
			}
		}

		HTTPRadioButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				closeDialog();
				mSkyElement.background = "sky.Background(sky.BackgroundType.HTTP)";
				mSkyElement.isAddBackground = true;

				new SkyBackgroundCodeCreator(file, clazz, "Sky Change Method", skyElement).execute();
			}
		});

		SINGLEWORKRadioButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				closeDialog();
				mSkyElement.background = "sky.Background(sky.BackgroundType.SINGLEWORK)";
				mSkyElement.isAddBackground = true;

				new SkyBackgroundCodeCreator(file, clazz, "Sky Change Method", skyElement).execute();
			}
		});

		WORKRadioButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				closeDialog();
				mSkyElement.background = "sky.Background(sky.BackgroundType.WORK)";
				mSkyElement.isAddBackground = true;

				new SkyBackgroundCodeCreator(file, clazz, "Sky Change Method", skyElement).execute();
			}
		});
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}

	private void checkRepeat() {
		mSkyElement.repeat = repeatCheckBox.isSelected();

		if (repeatCheckBox.isSelected()) {
			mSkyElement.repeatS = "sky.Repeat(true)";
		}
	}

	public class RepeatListener implements ChangeListener {

		@Override public void stateChanged(ChangeEvent event) {
			checkRepeat();
		}
	}

}
