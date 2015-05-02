/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 15 avr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.test.runtime.mock;

import java.util.Collection;
import java.util.Map;

import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.runtime.IFeedableSwingPage;
import com.synaptix.toast.core.runtime.IFeedableWebPage;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.core.runtime.ITestManager;

public class DefaultRepositorySetup implements IRepositorySetup{

	private Map<String, Object> userVariables;

	@Override
	public IFeedableSwingPage getSwingPage(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IFeedableSwingPage> getSwingPages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITestManager getTestManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSwingPage(String fixtureName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPage(String fixtureName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TestResult addClass(String className, String testName, String searchBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestResult addService(String testName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestResult addDomain(String domainClassName, String domainTestName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestResult addProperty(String componentName, String testName, String systemName, String componentAssociation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestResult insertComponent(String entityName2, Map<String, String> values2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getService(String fixtureName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFeedableWebPage getPage(String fixtureName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserVariables(Map<String, Object> userVariables) {
		this.userVariables= userVariables;
	}

	@Override
	public Map<String, Object> getUserVariables() {
		return userVariables;
	}

}
