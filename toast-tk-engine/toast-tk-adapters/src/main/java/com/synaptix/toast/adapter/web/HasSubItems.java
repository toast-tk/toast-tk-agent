package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

public interface HasSubItems {
	public void clickOn(String itemName) throws TimeoutException;
}
