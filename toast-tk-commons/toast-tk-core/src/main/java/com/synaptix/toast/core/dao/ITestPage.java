/**
 * 
 */
package com.synaptix.toast.core.dao;

import java.util.List;

import org.joda.time.LocalDateTime;

import com.synaptix.toast.core.report.TestResult;


public interface ITestPage extends ITaggable {
	
	public IBlock getVarBlock();
	
	public List<IBlock> getBlocks();

	public String getPageName();

	public LocalDateTime getStartDateTime();

	public long getExecutionTime();

	public int getTechnicalErrorNumber();

	public int getTestFailureNumber();

	public int getTestSuccessNumber();

	public boolean isSuccess();

	public String getName();

	public void setId(Object object);

	public boolean isPreviousIsSuccess();

	public long getPreviousExecutionTime();

	public void startExecution();

	public void addResult(TestResult result);

	public void stopExecution();

	public Object getParsingErrorMessage();

}
