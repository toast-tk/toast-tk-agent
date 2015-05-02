package com.synaptix.toast.automation.test;

import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.automation.repository.WebRepository;

/**
 * abstraction for test cases dealing with selenium
 * 
 * @author skokaina
 * 
 * @param <D>
 * @param <W>
 */
public abstract class SeleniumTestScriptBase<D extends SynchronizedDriver, W extends WebRepository<?>> extends TestScriptBase<D, W> {

}
