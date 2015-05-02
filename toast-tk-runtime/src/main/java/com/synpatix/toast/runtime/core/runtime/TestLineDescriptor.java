/**
 * 
 */
package com.synpatix.toast.runtime.core.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;

public class TestLineDescriptor {

	private String regex = "@(" + ActionAdapterKind.swing.name()+ "|" + ActionAdapterKind.web.name() + "|" +  ActionAdapterKind.service.name() + "):?([\\w]*)([\\w\\W]+)"; 
	
	public final TestLine testLine;
	private String testLineAction;
	private String testLineFixtureName;
	private ActionAdapterKind testLineFixtureKind;
	
	public TestLineDescriptor(TestBlock testBlock, TestLine testLine){
		this.testLine = testLine;
		initServiceKind(testBlock, testLine);
	}

	private void initServiceKind(TestBlock testBlock, TestLine testLine) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(testLine.getTest());
		if(matcher.find()){
			setTestLineFixtureKind(ActionAdapterKind.valueOf(matcher.group(1)));
			setTestLineFixtureName(matcher.group(2));
			setTestLineAction(matcher.group(3));
		}else{
			setTestLineFixtureKind(ActionAdapterKind.valueOf(testBlock.getFixtureName())); // exception otherwise !!
			setTestLineAction(testLine.getTest());
		}
	}

	private void setTestLineFixtureName(String testLineFixtureName) {
		this.testLineFixtureName = testLineFixtureName;
	}
	

	public String getTestLineFixtureName() {
		return testLineFixtureName == null ? "" : testLineFixtureName;
	}

	private void setTestLineAction(String testLineAction) {
		this.testLineAction = testLineAction;
	}

	public String getTestLineAction() {
		return testLineAction;
	}

	public ActionAdapterKind getTestLineFixtureKind() {
		return testLineFixtureKind;
	}

	public void setTestLineFixtureKind(ActionAdapterKind testLineFixtureKind) {
		this.testLineFixtureKind = testLineFixtureKind;
	}

	public boolean isSynchronizedCommand(){
		return testLineAction.endsWith(" !");
	}
	
	public boolean isFailFatalCommand(){
		return testLineAction.startsWith("* ");
	}
	
	public String getCommand(){
		String command = testLineAction;
		if(isFailFatalCommand()){
			command = command.substring(2);
		}
		//command = command.trim().replace("*", "");
		return command;
	}

}