package com.synaptix.toast.core.dao;

import java.util.List;

import org.joda.time.LocalDateTime;

public interface ITestPage extends ITaggable {

	public IBlock getVarBlock();

	public List<IBlock> getBlocks();

	public String getPageName();

	public LocalDateTime getStartDateTime();

	public long getExecutionTime();

	public int getTechnicalErrorNumber();

	public int getTestFailureNumber();

	public int getTestSuccessNumber();

	public void setTechnicalErrorNumber(
		int technicalErrorNumber);

	public void setTestFailureNumber(
		int testFailureNumber);

	public void setTestSuccessNumber(
		int testSuccessNumber);

	// addResult (find a better way to report success/failure/error)
	public boolean isSuccess();

	public String getName();

	public void setId(
		String id);

	public boolean isPreviousIsSuccess();
	
	public void setPreviousIsSuccess(boolean isSuccess);

	public long getPreviousExecutionTime();

	public void startExecution();

	public void stopExecution();

	public Object getParsingErrorMessage();

	public String getIdAsString();

	void setPreviousExecutionTime(
		long previousExecutionTime);
}
