package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.runtime.ErrorResultReceivedException;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasValueBase<T> {

	public T getValue()
		throws IllegalAccessException, TimeoutException, ErrorResultReceivedException;
}
