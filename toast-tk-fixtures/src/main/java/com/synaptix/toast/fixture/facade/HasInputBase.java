package com.synaptix.toast.fixture.facade;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasInputBase<T> {
	public void setInput(T e);

	public T getValue();
}
