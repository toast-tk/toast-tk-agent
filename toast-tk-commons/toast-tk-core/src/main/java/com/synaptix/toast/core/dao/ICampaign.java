package com.synaptix.toast.core.dao;

import java.util.List;

public interface ICampaign extends ITaggable {

	List<ITestPage> getTestCases();

	Object getName();

	void setId(Object object);

}
