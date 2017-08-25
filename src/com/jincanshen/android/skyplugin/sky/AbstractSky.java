package com.jincanshen.android.skyplugin.sky;

import java.util.regex.Pattern;

public abstract class AbstractSky implements ISky {

	private static final String	mPackageName					= "sky";

	private final Pattern		mMethodAnnotationPattern		= Pattern.compile("^@" + getMethodAnnotation() + "\\(([^\\)]+)\\)$", Pattern.CASE_INSENSITIVE);

	private final String		mMethodAnnotationCanonicalName	= getPackageName() + "." + getMethodAnnotation();

	@Override public String getPackageName() {
		return mPackageName;
	}

	@Override public Pattern getAnnotationPattern() {
		return mMethodAnnotationPattern;
	}

	@Override public String getMethodAnnotationCanonicalName() {
		return mMethodAnnotationCanonicalName;
	}
}
