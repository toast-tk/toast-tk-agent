package com.synaptix.toast.dao.domain.impl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.dao.domain.api.test.IRunnableTest;
import com.synaptix.toast.dao.domain.impl.common.BasicEntityBean;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;

@Entity(value = "test")
@Indexes({
		@Index(value = "pageName, -runDateTime"), @Index("runDateTime"), @Index("isTemplate")
})
public class TestPage extends BasicEntityBean implements IBlock, IRunnableTest, ITestPage {

	@Embedded
	private TestResult testResult;

	@Transient
	private File file;

	@Embedded
	private List<IBlock> blocks;

	private int technicalErrorNumber;

	private int testSuccessNumber;

	private int testFailureNumber;

	private String pageName;

	private String parsingErrorMessage;

	private long runDateTime;

	private long executionTime;

	private long previousExecutionTime;

	private boolean previousIsSuccess;

	private boolean isTemplate;

	public TestPage() {
		blocks = new ArrayList<IBlock>();
	}
	
	@Override
	public String getIdAsString() {
		return id != null ? id.toString() : null;
	}
	
	@Override
	public void setId(
		String id) {
		if(id == null) {
			this.id = null;
		}else{
			this.id = new ObjectId(id);
		}
	}

	public int getTechnicalErrorNumber() {
		return technicalErrorNumber;
	}

	public void setTechnicalErrorNumber(
		int technicalErrorNumber) {
		this.technicalErrorNumber = technicalErrorNumber;
	}

	public int getTestSuccessNumber() {
		return testSuccessNumber;
	}

	public void setTestSuccessNumber(
		int testSuccessNumber) {
		this.testSuccessNumber = testSuccessNumber;
	}

	public int getTestFailureNumber() {
		return testFailureNumber;
	}

	public void setTestFailureNumber(
		int testFailureNumber) {
		this.testFailureNumber = testFailureNumber;
	}

	public String getPath() {
		return pageName;
	}

	public void setPath(
			String pageName) {
		this.pageName = pageName;
	}

	/**
	 * @param currentTestBlock
	 */
	public void addBlock(
		IBlock testBlock) {
		blocks.add(testBlock);
	}

	public String getParsingErrorMessage() {
		return parsingErrorMessage;
	}

	public void setParsingErrorMessage(
		String parsingErrorMessage) {
		this.parsingErrorMessage = parsingErrorMessage;
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(
		long executionTime) {
		this.executionTime = executionTime;
	}

	public File getFile() {
		return file;
	}

	public void setFile(
		File file) {
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synpatix.redpepper.backend.core.IRunnableTest#getTestResult()
	 */
	@Override
	public TestResult getTestResult() {
		return this.testResult;
	}

	@Override
	public void setTestResult(
		TestResult testResult) {
		this.testResult = testResult;
	}

	@Override
	public void startExecution() {
		this.runDateTime = System.currentTimeMillis();
		setPreviousIsSuccess(isSuccess());
		previousExecutionTime = executionTime;
	}

	@Override
	public void stopExecution() {
		this.executionTime = System.currentTimeMillis() - runDateTime;
	}

	@Override
	public LocalDateTime getStartDateTime() {
		return new LocalDateTime(this.runDateTime);
	}

	public List<IBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(
		List<IBlock> blocks) {
		this.blocks = blocks;
	}

	@Override
	@PrePersist
	public void prePersist() {
		this.name = this.pageName;
	}

	public boolean isSuccess() {
		return (this.technicalErrorNumber + this.testFailureNumber) == 0;
	}

	@Override
	public long getPreviousExecutionTime() {
		return previousExecutionTime;
	}

	@Override
	public void setPreviousExecutionTime(
		long previousExecutionTime) {
		this.previousExecutionTime = previousExecutionTime;
	}

	@Override
	public boolean isPreviousIsSuccess() {
		return previousIsSuccess;
	}

	@Override
	public void setPreviousIsSuccess(
		boolean previousIsSuccess) {
		this.previousIsSuccess = previousIsSuccess;
	}

	public void setIsTemplate(
		boolean b) {
		this.isTemplate = b;
	}

	public boolean getIsTemplate() {
		return this.isTemplate;
	}

	@Override
	public IBlock getVarBlock() {
		for(IBlock block : blocks) {
			if(block instanceof VariableBlock) {
				return block;
			}
		}
		return null;
	}

	@Override
	public String getBlockType() {
		return "testPageBlock";
	}

	@Override
	public int getNumberOfLines() {
		return 0;
	}
}
