package com.jincanshen.android.skyplugin.sky;

public class Sky3 extends AbstractSky {

	private static final String mMethodAnnotation = "Background";

	@Override public String getVersion() {
		return "3.0.0";
	}

	@Override public String getMethodAnnotation() {
		return mMethodAnnotation;
	}

	@Override public String getDistinctClassName() {
		return getMethodAnnotationCanonicalName();
	}

}
