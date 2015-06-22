package com.synaptix.toast.utils;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	public static String getFileContentFromStream(InputStream stream) {
		int len = 0;
		byte[] buffer = new byte[8192];
		StringBuffer b = null;
		try {
			b = new StringBuffer();
			while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
				b.append(new String(buffer));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b != null ? b.toString() : null;
	}
}
