package com.synaptix.toast.plugin.synaptix.runtime.helper;

public final class StringAppender {

	private StringAppender() {

	}

	public static int size(final String curStr) {
		return curStr != null ? curStr.length() : 0;
	}

	public static String concatAlloc(final Object...strs) {
		if (strs != null) {
            final int nbStr = strs.length;
            if (nbStr > 0) {
                int globalSize = 0;
                final String[] localStringArray = new String[nbStr];
                String curStr;
                Object curObj;
                for (int index = 0; index < nbStr; ++index) {
                    curObj = strs[index];
                    if (curObj != null) {
                        curStr = curObj.toString();
                        localStringArray[index] = curStr;
                        globalSize += size(curStr);
                    } else {
                        localStringArray[index] = null;
                    }

                }
                final StringBuilder sb = new StringBuilder(globalSize);
                for (int index = 0; index < nbStr; ++index) {
                    sb.append(localStringArray[index]);
                }
                return sb.toString();
            }
        }
        return null;
	}
}