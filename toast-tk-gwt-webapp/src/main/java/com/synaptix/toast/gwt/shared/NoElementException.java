package com.synaptix.toast.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoElementException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoElementException(String key) {
		super("No Element found: " + key);
	}

	public NoElementException() {
		super("No Element found");
	}

}
