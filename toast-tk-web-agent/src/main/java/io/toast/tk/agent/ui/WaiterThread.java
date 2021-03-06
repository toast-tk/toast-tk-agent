package io.toast.tk.agent.ui;

import java.io.IOException;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.run.TestRunner;

public class WaiterThread implements Runnable {
	  private WaiterPanel panel;
	  private Thread thread;
	  private TestRunner testrunner;
	   
	  public WaiterThread(AgentConfigProvider webConfigProvider) throws IOException { 
		  	WaiterPanel waiterPanel = new WaiterPanel();
			this.panel = waiterPanel;
			this.testrunner = new TestRunner(webConfigProvider);   
			this.thread = new Thread(new RunnerThread(testrunner));      
	  }
	  
	  public void run() {
		  	thread.start();
	  		while(thread.isAlive()) {
	  			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
	  			
	  			if(panel.isInterupted()) {
	  				thread.interrupt();
	  			  	panel.setScript(testrunner.fileName, "Interupting");
	  			  	testrunner.kill();
	  			}
	  			else {
	  				panel.setScript(testrunner.fileName, "In progress");
	  			}
	  		}
	  		panel.stop();
	  		this.kill();
	  }
	  
	  private void kill() {
		  Thread.currentThread().interrupt();
		  panel = null;
		  thread.interrupt();
		  thread = null;
		  testrunner.kill();
		  testrunner = null;
	  }
	  
	  protected class RunnerThread implements Runnable {
		  public TestRunner runner;
		   
		  public RunnerThread(TestRunner testrunner) throws IOException {           
				this.runner = testrunner;
		  }
		  
		  public void run() {
	      		runner.execute();
		  }
		  
		  public void kill() {
			  runner.kill();
			  runner = null;
		  }
		}
	}