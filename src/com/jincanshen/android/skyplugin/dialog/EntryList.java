package com.jincanshen.android.skyplugin.dialog;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.ui.components.JBScrollPane;
import com.jincanshen.android.skyplugin.dialog.interfaces.ICancelListener;
import com.jincanshen.android.skyplugin.dialog.interfaces.IConfirmListener;
import com.jincanshen.android.skyplugin.dialog.interfaces.OnCheckBoxStateChangedListener;
import com.jincanshen.android.skyplugin.model.SkyElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EntryList extends JPanel {

	protected Project						mProject;

	protected Editor						mEditor;

	protected ArrayList<SkyElement>			mElements			= new ArrayList<>();

	protected ArrayList<Entry>				mEntries			= new ArrayList<>();

	protected IConfirmListener				mConfirmListener;

	protected ICancelListener				mCancelListener;


	protected boolean						isInheritor;

	protected JCheckBox						mHolderCheck;

	protected JLabel						mHolderLabel;

	protected JCheckBox						msplitOnclickMethodsCheck;

	protected JButton						mConfirm;

	protected JButton						mCancel;

	protected DialogHeader					mEntryHeader;

	private OnCheckBoxStateChangedListener	allCheckListener	= new OnCheckBoxStateChangedListener() {

																	@Override public void changeState(boolean checked) {
																		for (final Entry entry : mEntries) {
																			entry.setListener(null);
																			entry.getCheck().setSelected(checked);
																			entry.setListener(singleCheckListener);
																			entry.setClick(checked);
																		}
																	}
																};

	private OnCheckBoxStateChangedListener	singleCheckListener	= new OnCheckBoxStateChangedListener() {

																	@Override public void changeState(boolean checked) {
																		boolean result = true;
																		for (Entry entry : mEntries) {
																			result &= entry.getCheck().isSelected();
																		}

																		mEntryHeader.setAllListener(null);
																		mEntryHeader.getAllCheck().setSelected(result);
																		mEntryHeader.setAllListener(allCheckListener);
																	}
																};

	public EntryList(Project project, Editor editor, ArrayList<SkyElement> elements, IConfirmListener confirmListener, ICancelListener cancelListener) {
		mProject = project;
		mEditor = editor;
		mConfirmListener = confirmListener;
		mCancelListener = cancelListener;
		if (elements != null) {
			mElements.addAll(elements);
		}
		setPreferredSize(new Dimension(640, 360));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		addInjections();
		addButtons();
	}

	protected void addInjections() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mEntryHeader = new DialogHeader();
		contentPanel.add(mEntryHeader);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		JPanel injectionsPanel = new JPanel();
		injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));
		injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		int cnt = 0;
		boolean selectAllCheck = true;
		for (SkyElement element : mElements) {
			Entry entry = new Entry(this, element);
			entry.setListener(singleCheckListener);

			if (cnt > 0) {
				injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			}
			injectionsPanel.add(entry);
			cnt++;

			mEntries.add(entry);

			selectAllCheck &= entry.getCheck().isSelected();
		}
		mEntryHeader.getAllCheck().setSelected(selectAllCheck);
		mEntryHeader.setAllListener(allCheckListener);
		injectionsPanel.add(Box.createVerticalGlue());
		injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
		contentPanel.add(scrollPane);

		add(contentPanel, BorderLayout.CENTER);
		refresh();
	}

	protected void addButtons() {
		/*
		 * mPrefixCheck = new JCheckBox(); mPrefixCheck.setPreferredSize(new
		 * Dimension(32, 26)); mPrefixCheck.addChangeListener(new
		 * CheckPrefixListener()); mPrefixValue = new
		 * JTextField(Utils.getPrefix(), 10); mPrefixValue.setPreferredSize(new
		 * Dimension(40, 26)); mPrefixLabel = new JLabel();
		 * mPrefixLabel.setText("Field name prefix"); JPanel prefixPanel = new
		 * JPanel(); prefixPanel.setLayout(new BoxLayout(prefixPanel,
		 * BoxLayout.LINE_AXIS));
		 * prefixPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		 * prefixPanel.add(mPrefixCheck); prefixPanel.add(mPrefixValue);
		 * prefixPanel.add(mPrefixLabel);
		 * prefixPanel.add(Box.createHorizontalGlue()); add(prefixPanel,
		 * BorderLayout.PAGE_END);
		 */

		mHolderCheck = new JCheckBox();
		mHolderCheck.setPreferredSize(new Dimension(32, 26));
		mHolderCheck.addChangeListener(new CheckHolderListener());

		mHolderLabel = new JLabel();
		mHolderLabel.setText("Extends SkyBiz");

		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
		holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        holderPanel.add(mHolderCheck);
        holderPanel.add(mHolderLabel);
		holderPanel.add(Box.createHorizontalGlue());
		add(holderPanel, BorderLayout.PAGE_END);

		msplitOnclickMethodsCheck = new JCheckBox();
		msplitOnclickMethodsCheck.setPreferredSize(new Dimension(32, 26));
		msplitOnclickMethodsCheck.setSelected(false);

		mCancel = new JButton();
		mCancel.setAction(new CancelAction());
		mCancel.setPreferredSize(new Dimension(120, 26));
		mCancel.setText("Cancel");
		mCancel.setVisible(true);

		mConfirm = new JButton();
		mConfirm.setAction(new ConfirmAction());
		mConfirm.setPreferredSize(new Dimension(120, 26));
		mConfirm.setText("Confirm");
		mConfirm.setVisible(true);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(mCancel);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(mConfirm);

		add(buttonPanel, BorderLayout.PAGE_END);
		refresh();
	}

	protected void refresh() {
		revalidate();

		if (mConfirm != null) {
			mConfirm.setVisible(mElements.size() > 0);
		}
	}

	protected boolean checkValidity() {
		boolean valid = true;

		for (SkyElement element : mElements) {
			// if (!element.checkValidity()) {
			// valid = false;
			// }
		}

		return valid;
	}

	public JButton getConfirmButton() {
		return mConfirm;
	}
	// classes

	protected class ConfirmAction extends AbstractAction {

		public void actionPerformed(ActionEvent event) {
			boolean valid = checkValidity();

			for (Entry entry : mEntries) {
				entry.syncElement();
			}

			if (valid) {
				if (mConfirmListener != null) {
					mConfirmListener.onConfirm(mProject, mEditor, mElements, isInheritor);
				}
			}
		}
	}

	protected class CancelAction extends AbstractAction {

		public void actionPerformed(ActionEvent event) {
			if (mCancelListener != null) {
				mCancelListener.onCancel();
			}
		}
	}

	public class CheckHolderListener implements ChangeListener {

		@Override public void stateChanged(ChangeEvent event) {
			isInheritor = mHolderCheck.isSelected();
		}
	}
}
