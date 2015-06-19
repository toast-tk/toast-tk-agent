package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.runtime.ErrorResultReceivedException;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasInputBase<T> extends HasValueBase<T> {
	public void setInput(T e) throws TimeoutException, ErrorResultReceivedException;
}
