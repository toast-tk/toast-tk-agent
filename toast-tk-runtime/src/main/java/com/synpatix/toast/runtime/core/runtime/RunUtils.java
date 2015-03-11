package com.synpatix.toast.runtime.core.runtime;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.toast.dao.domain.impl.test.TestPage;

public class RunUtils {

	public static void printResult(List<TestPage> testPages){
		int totalErrors = 0;
		int totalTechnical = 0;
		int totalSuccess = 0;
		List<String> filesWithErrorsList = new ArrayList<String>();
		for (TestPage testPage : testPages) {
			totalErrors += testPage.getTestFailureNumber();
			totalTechnical += testPage.getTechnicalErrorNumber();
			if (testPage.getTechnicalErrorNumber() > 0) {
				filesWithErrorsList.add(testPage.getName());
			}
			totalSuccess += testPage.getTestSuccessNumber();
		}
		if (totalErrors > 0) {
			System.out.println("Files with failed tests : ");
			for (String string : filesWithErrorsList) {
				System.out.println("- " + string);
			}
		}

		System.out.println("Technical errors: " + totalTechnical);
		System.out.println("Failures: " + totalErrors);
		System.out.println("Success: " + totalSuccess);
	}
	
}
