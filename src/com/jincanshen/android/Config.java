package com.jincanshen.android;

import com.intellij.ide.util.PropertiesComponent;

public class Config {

	private static Config	config;

	private boolean			isModule					= false;

	private boolean			isCreateHolder				= false;

	private boolean			isMsplitOnclickMethodsCheck	= false;

	private boolean			isChangeSkyState			= false;	// 状态

	private Config() {

	}

	public void save() {
		PropertiesComponent.getInstance().setValue("isModule", isModule);
		PropertiesComponent.getInstance().setValue("isCreateHolder", isCreateHolder);
		PropertiesComponent.getInstance().setValue("isMsplitOnclickMethodsCheck", isMsplitOnclickMethodsCheck);
		PropertiesComponent.getInstance().setValue("isChangeSkyState", isChangeSkyState);
	}

	public static Config getInstant() {
		if (config == null) {
			config = new Config();
			config.setModule(PropertiesComponent.getInstance().getBoolean("isModule", false));
			config.setCreateHolder(PropertiesComponent.getInstance().getBoolean("isCreateHolder", false));
			config.setMsplitOnclickMethodsCheck(PropertiesComponent.getInstance().getBoolean("isMsplitOnclickMethodsCheck", false));
			config.setChangeSkyState(PropertiesComponent.getInstance().getBoolean("isChangeSkyState", false));
		}
		return config;
	}

	public void setModule(boolean module) {
		this.isModule = module;
	}

	public boolean isModule() {
		return this.isModule;
	}

	public void setCreateHolder(boolean createHolder) {
		this.isCreateHolder = createHolder;
	}

	public boolean isCreateHolder() {
		return this.isCreateHolder;
	}

	public void setMsplitOnclickMethodsCheck(boolean msplitOnclickMethodsCheck) {
		this.isMsplitOnclickMethodsCheck = msplitOnclickMethodsCheck;
	}

	public boolean isMsplitOnclickMethodsCheck() {
		return this.isMsplitOnclickMethodsCheck;
	}

	public void setChangeSkyState(boolean isState) {
		this.isChangeSkyState = isState;
	}

	public boolean isChangeSkyState(){
		return this.isChangeSkyState;
	}
}
