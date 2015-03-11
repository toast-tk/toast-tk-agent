package com.synaptix.toast.plugin.synaptix.runtime.split;

public final class Split {

	private static final String SEPARATOR = ":";

	private Split() {

	}

	public static String[] split(final String str) {
		return str.split(SEPARATOR);
	}

	public static void add(
			final StringBuilder sb,
			final String str
	) {
		sb.append(str);
	}

	public static void add(
			final StringBuilder sb,
			final Object obj
	) {
		add(sb, String.valueOf(obj));
	}

	public static void addWithSeparator(
			final StringBuilder sb,
			final String str
	) {
		sb.append(str).append(SEPARATOR);
	}

	public static void addWithSeparator(
			final StringBuilder sb,
			final Object obj
	) {
		addWithSeparator(sb, String.valueOf(obj));
	}

	public static void clean(final StringBuilder sb) {
		final int size = sb.length() - SEPARATOR.length();
		if(size > 0) {
			sb.setLength(size);
		}
	}
}