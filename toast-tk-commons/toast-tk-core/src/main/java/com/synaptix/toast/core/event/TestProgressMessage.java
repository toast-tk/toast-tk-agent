package com.synaptix.toast.core.event;

import com.synaptix.toast.core.dao.ITestPage;

public class TestProgressMessage {
	
	private ITestPage page;


	protected TestProgressMessage(){
		
	}

	
	public TestProgressMessage(ITestPage page){
		this.setPage(page);
	}


	public ITestPage getPage() {
		return page;
	}


	private void setPage(ITestPage page) {
		this.page = page;
	}

}
