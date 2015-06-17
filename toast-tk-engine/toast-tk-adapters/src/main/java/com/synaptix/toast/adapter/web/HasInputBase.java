package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasInputBase<T> extends HasValueBase<T> {
	public void setInput(T e) throws TimeoutException;
}
