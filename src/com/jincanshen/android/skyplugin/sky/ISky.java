package com.jincanshen.android.skyplugin.sky;

import java.util.regex.Pattern;

public interface ISky {

	/**
	 * 包名
	 *
	 * @return Package name
	 */
	String getPackageName();

	/**
	 * 方法上的注解
	 *
	 * @return The pattern
	 */

	String getMethodAnnotation();

	/**
	 * 方法上的注解
	 * 
	 * @return
	 */
	Pattern getAnnotationPattern();

	/**
	 * 得到不同的类
	 * 
	 * @return
	 */
	String getDistinctClassName();

	/**
	 * 版本
	 * 
	 * @return
	 */
	String getVersion();

	/**
	 * 注解方法 验证
	 * 
	 * @return
	 */
	String getMethodAnnotationCanonicalName();

}
