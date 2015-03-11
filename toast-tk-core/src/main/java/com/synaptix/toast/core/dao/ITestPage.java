/**
 * 
 */
package com.synaptix.toast.core.dao;

import java.util.List;

import org.joda.time.LocalDateTime;


public interface ITestPage extends ITaggable {
	
	public List<IBlock> getBlocks();

	public String getPageName();

	public LocalDateTime getStartDateTime();

	public long getExecutionTime();

	public int getTechnicalErrorNumber();

	public int getTestFailureNumber();

	public int getTestSuccessNumber();

}
