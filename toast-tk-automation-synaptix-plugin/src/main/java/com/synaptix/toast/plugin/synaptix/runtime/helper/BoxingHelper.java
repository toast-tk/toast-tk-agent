package com.synaptix.toast.plugin.synaptix.runtime.helper;

public final class BoxingHelper {

	private BoxingHelper() {

	}
	
	public static int unbox(final Object integer) {
		if(integer instanceof Integer) {
			return unbox((Integer) integer); 
		}
		return 0;
	}
	
	private static int unbox(final Integer integer) {
		return integer.intValue();
	}
}