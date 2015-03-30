/**
 * 
 */
package com.synpatix.toast.runtime.core.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synaptix.toast.core.annotation.FixtureKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;

public class TestLineDescriptor {

	private String regex = "@(" + FixtureKind.swing.name()+ "|" + FixtureKind.web.name() + "|" +  FixtureKind.service.name() + ")([\\w\\W]+)"; 
	
	public final TestLine testLine;
	private String testLineAction;
	private FixtureKind testLineFixtureKind;
	
	public TestLineDescriptor(TestBlock testBlock, TestLine testLine){
		this.testLine = testLine;
		initServiceKind(testBlock, testLine);
	}

	private void initServiceKind(TestBlock testBlock, TestLine testLine) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(testLine.getTest());
		if(matcher.find()){
			setTestLineFixtureKind(FixtureKind.valueOf(matcher.group(1)));
			setTestLineAction(matcher.group(2));
		}else{
			setTestLineFixtureKind(FixtureKind.valueOf(testBlock.getFixtureName())); // exception otherwise !!
			setTestLineAction(testLine.getTest());
		}
	}

	private void setTestLineAction(String testLineAction) {
		this.testLineAction = testLineAction;
	}

	public String getTestLineAction() {
		return testLineAction;
	}

	public FixtureKind getTestLineFixtureKind() {
		return testLineFixtureKind;
	}

	public void setTestLineFixtureKind(FixtureKind testLineFixtureKind) {
		this.testLineFixtureKind = testLineFixtureKind;
	}

}