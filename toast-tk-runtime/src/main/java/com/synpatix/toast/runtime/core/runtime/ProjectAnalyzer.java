package com.synpatix.toast.runtime.core.runtime;

import java.io.File;

public class ProjectAnalyzer {
	public static void main(String[] args) {
		String sfile = ProjectAnalyzer.class.getClassLoader().getResource("hello.txt").getFile();
		File file = new File(sfile);
	}
}
