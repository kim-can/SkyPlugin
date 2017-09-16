package com.jincanshen.android.skyplugin.dialog;

import com.intellij.openapi.ui.ComboBox;
import com.jincanshen.android.skyplugin.dialog.interfaces.OnCheckBoxStateChangedListener;
import com.jincanshen.android.skyplugin.model.SkyElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Entry extends JPanel {

	protected EntryList							mParent;

	protected SkyElement						mElement;

	protected OnCheckBoxStateChangedListener	mListener;

	// ui
	protected JCheckBox							mCheck;

	protected JLabel							mMethodName;

	protected JCheckBox							mRepeat;

	protected ComboBox							mBackground;

	protected JTextField						mName;

	protected Color								mNameDefaultColor;

	public JCheckBox getCheck() {
		return mCheck;
	}

	public void setListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
		this.mListener = onStateChangedListener;
	}

	public Entry(EntryList parent, SkyElement element) {
		mElement = element;
		mParent = parent;

		mCheck = new JCheckBox();
		mCheck.setPreferredSize(new Dimension(40, 26));
		mCheck.setSelected(mElement.isClick);
		mCheck.addChangeListener(new CheckListener());

		mMethodName = new JLabel(element.methodName);
		mMethodName.setPreferredSize(new Dimension(100, 26));

		mRepeat = new JCheckBox("Repeat");
		mRepeat.setPreferredSize(new Dimension(100, 26));
		mRepeat.setSelected(mElement.repeat);
		mRepeat.addChangeListener(new RepeatListener());

		String[] s = { "", "HTTP", "SINGLEWORK", "WORK" };
		mBackground = new ComboBox(s);

		if (element.isAddBackground && element.background != null) {
			if (element.background.indexOf(s[1]) != -1) {
				mBackground.setSelectedIndex(1);
			} else if (element.background.indexOf(s[2]) != -1) {
				mBackground.setSelectedIndex(2);
			} else if (element.background.indexOf(s[3]) != -1) {
				mBackground.setSelectedIndex(3);
			}
		}

		mBackground.setPreferredSize(new Dimension(120, 26));
		mBackground.addItemListener(new BackgroundListenner());

		mName = new JTextField(mElement.methodName, 10);
		mNameDefaultColor = mName.getBackground();
		mName.setPreferredSize(new Dimension(100, 26));
		mName.addFocusListener(new FocusListener() {

			@Override public void focusGained(FocusEvent e) {
				// empty
			}

			@Override public void focusLost(FocusEvent e) {
				syncElement();
			}
		});

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setMaximumSize(new Dimension(Short.MAX_VALUE, 54));
		add(mCheck);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(mMethodName);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(mRepeat);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(mBackground);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(mName);
		add(Box.createHorizontalGlue());

		checkState();
	}

	public void syncElement() {
		mElement.methodName = mName.getText();
	}

	private void checkState() {
		if (mCheck.isSelected()) {
			mRepeat.setEnabled(true);
			mBackground.setEnabled(true);
			mName.setEnabled(true);
		} else {
			mRepeat.setEnabled(false);
			mBackground.setEnabled(false);
			mName.setEnabled(false);
		}

		setClick(mCheck.isSelected());

		if (mListener != null) {
			mListener.changeState(mCheck.isSelected());
		}
	}

	private void checkRepeat() {
		mElement.repeat = mRepeat.isSelected();

		if (mRepeat.isSelected()) {
			mElement.repeatS = "sky.Repeat(true)";
		}
	}

	public void setClick(boolean click) {
		mElement.isClick = click;
	}

	// classes

	public class CheckListener implements ChangeListener {

		@Override public void stateChanged(ChangeEvent event) {
			checkState();
		}
	}

	public class RepeatListener implements ChangeListener {

		@Override public void stateChanged(ChangeEvent event) {
			checkRepeat();
		}
	}

	public class BackgroundListenner implements ItemListener {

		@Override public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String value = (String) e.getItem();
				if (value == null || value.length() < 1) {
					mElement.background = "";
					mElement.isAddBackground = false;
				} else {
					mElement.background = "sky.Background(sky.BackgroundType." + value + ")";
					mElement.isAddBackground = true;
				}
			}
		}
	}
}
