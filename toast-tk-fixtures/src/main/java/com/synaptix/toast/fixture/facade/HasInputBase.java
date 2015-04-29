package com.synaptix.toast.fixture.facade;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasInputBase<T> extends HasValueBase<T> {
	public void setInput(T e);
}
