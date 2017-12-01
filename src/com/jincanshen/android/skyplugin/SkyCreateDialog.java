package com.jincanshen.android.skyplugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.jincanshen.android.skyplugin.code.SkyCreateFileCodeCreator;

import javax.swing.*;
import java.awt.event.*;

public class SkyCreateDialog extends JDialog {

	private JPanel			contentPane;

	private JButton			buttonOK;

	private JButton			buttonCancel;

	private JTextField		textField1;

	private JComboBox		comboBox1;

	private JRadioButton	viewRadioButton;

	private JRadioButton	adapterRadioButton;

	private Project			project;

	private PsiDirectory	psiDirectory;

	ButtonGroup				buttonGroup;

	public int				selectType;

	String					packageName;

	private SkyCreateDialog() {}

	public SkyCreateDialog(Project project, PsiDirectory psiDirectory) {
		this.project = project;
		this.psiDirectory = psiDirectory;

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		viewRadioButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				comboBox1.removeAllItems();
				selectType = 0;
				comboBox1.addItem("SkyActivity");
				comboBox1.addItem("SkyFragment");
				comboBox1.addItem("SkyDialogFragment");

			}
		});

		adapterRadioButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				comboBox1.removeAllItems();
				selectType = 1;
				comboBox1.addItem("SkyRVAdapter");
				comboBox1.addItem("SkyRVAdapterMore");
			}
		});

		buttonGroup = new ButtonGroup();
		buttonGroup.add(viewRadioButton);
		buttonGroup.add(adapterRadioButton);

		selectType = 0;
	}

	private void onOK() {
		dispose();
		new SkyCreateFileCodeCreator(project, psiDirectory, selectType,packageName, textField1.getText(), comboBox1.getSelectedItem().toString(), comboBox1.getSelectedIndex(), "sky create class").execute();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	public static void main(String[] args) {
		SkyCreateDialog dialog = new SkyCreateDialog();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}
}
