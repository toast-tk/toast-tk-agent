package com.synaptix.toast.test.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.test.runtime.mock.DefaultRepositorySetup;
import com.synpatix.toast.runtime.core.runtime.TestRunnerArgumentHelper;
import com.synpatix.toast.runtime.core.runtime.TestRunner;
import com.synpatix.toast.runtime.core.runtime.TestRunner.FixtureExecCommandDescriptor;

public class TestRunnerTestCase {
	
	class Tata {
		
	}
	
	class Toto extends Tata{
		
	}
	
	class Titi{
		@Action(action = "Titi", description = "")
		public void blabla(){
			
		}
	}
	
	
	@Test
	public void testEmptyResult() {
		TestRunner runner = new TestRunner(null, null);
		FixtureExecCommandDescriptor findMethodInClass = runner.findMethodInClass("Titi", Toto.class);
		assertNull(findMethodInClass);
	}
	
	@Test
	@Ignore
	public void testNonEmptyResult() {
		TestRunner runner = new TestRunner(null, null);
		FixtureExecCommandDescriptor findMethodInClass = runner.findMethodInClass("Titi", Titi.class);
		assertNotNull(findMethodInClass);
	}
	
	@Test
	@Ignore
	public void testArgumentBuild() {
		IRepositorySetup repo = new DefaultRepositorySetup();
		Map<String,Object> userVarMap = new HashMap<String, Object>();
		userVarMap.put("$variable", "200");
		repo.setUserVariables(userVarMap);
		Object buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "$variable");
		assertEquals(buildArgument, "200");
		buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "*$variable*");
		assertEquals(buildArgument, "200");
		buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "$vaiable");
		assertNull(buildArgument);
		buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "$$variable");
		assertEquals(buildArgument, "$variable");
		buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "*variable*");
		assertEquals(buildArgument, "variable");
	}
	
	@Test
	@Ignore
	public void testComplexArgumentBuild() {
		IRepositorySetup repo = new DefaultRepositorySetup();
		Map<String,Object> userVarMap = new HashMap<String, Object>();
		userVarMap.put("$var", "value");
		userVarMap.put("$variable", "nested $var replacement");
		repo.setUserVariables(userVarMap);
		Object buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "$variable");
		assertEquals(buildArgument, "nested value replacement");
	}
	
	@Test
	public void testComplexMultipleArgumentBuild() {
		IRepositorySetup repo = new DefaultRepositorySetup();
		Map<String,Object> userVarMap = new HashMap<String, Object>();
		userVarMap.put("$var", "value");
		userVarMap.put("$vari", "value");
		userVarMap.put("$variable", "nested $var replacement \n with another $vari");
		repo.setUserVariables(userVarMap);
		Object buildArgument = TestRunnerArgumentHelper.buildActionAdapterArgument(repo, "$variable");
		assertEquals(buildArgument, "nested value replacement \n with another value");
	}
	
	
	@AfterClass
	public static void end(){
	}
}
