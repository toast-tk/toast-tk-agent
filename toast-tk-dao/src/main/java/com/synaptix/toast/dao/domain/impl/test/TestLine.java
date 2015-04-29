/**
 * 
 */
package com.synaptix.toast.dao.domain.impl.test;

import org.joda.time.LocalDateTime;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.dao.domain.def.test.IRunnableTest;

/**
 * @author E413544
 * 
 */
@Entity(value = "test", noClassnameStored = true)
@Embedded
public class TestLine implements IRunnableTest {

	private String test;

	private String expected;

	@Embedded
	private TestResult testResult;

	/**
	 * Test comment
	 */
	private String comment;

	private long startTime = System.currentTimeMillis();

	private long executionTime = System.currentTimeMillis();

	public TestLine() {
	}

	public TestLine(String test, String expected, String comment) {
		this.setTest(test);
		this.setExpected(expected);
		this.setTestCommentString(comment);
	}

	@Override
	public TestResult getTestResult() {
		return testResult;
	}

	@Override
	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTestCommentString() {
		return comment;
	}

	public void setTestCommentString(String testCommentString) {
		this.comment = testCommentString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synpatix.redpepper.backend.core.IRunnableTest#startExecution()
	 */
	@Override
	public void startExecution() {
		startTime = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synpatix.redpepper.backend.core.IRunnableTest#stopExecution()
	 */
	@Override
	public void stopExecution() {
		executionTime = System.currentTimeMillis() - startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synpatix.redpepper.backend.core.IRunnableTest#getExecutionTime()
	 */
	@Override
	public long getExecutionTime() {
		return executionTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synpatix.redpepper.backend.core.IRunnableTest#getStartDateTime()
	 */
	@Override
	public LocalDateTime getStartDateTime() {
		return new LocalDateTime(startTime);
	}
}