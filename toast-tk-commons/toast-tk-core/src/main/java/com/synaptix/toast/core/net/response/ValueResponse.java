package com.synaptix.toast.core.net.response;

import com.synaptix.toast.core.net.request.IIdRequest;

/**
 * Created by skokaina on 07/11/2014.
 */
public class ValueResponse implements IIdRequest {
	private String id;
	public String value;

	/**
	 * serialization only
	 */
	public ValueResponse() {

	}

	public ValueResponse(String id, String b) {
		this.id = id;
		this.value = b;
	}

	@Override
	public String getId() {
		return id;
	}

}
