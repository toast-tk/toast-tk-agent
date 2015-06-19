package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.runtime.ErrorResultReceivedException;

public interface HasSubItems {
	public void clickOn(String itemName) throws TimeoutException, ErrorResultReceivedException;
}
