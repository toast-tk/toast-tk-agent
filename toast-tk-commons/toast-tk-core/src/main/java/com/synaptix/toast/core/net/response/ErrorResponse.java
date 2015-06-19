package com.synaptix.toast.core.net.response;

import java.awt.image.BufferedImage;

import com.synaptix.toast.core.net.request.IIdRequest;

/**
 * Created by skokaina on 07/11/2014.
 */
public class ErrorResponse implements IIdRequest {
	private String id;
	private String message;
	private BufferedImage screenshot;

	/**
	 * serialization only
	 */
	public ErrorResponse() {

	}

	public ErrorResponse(String id, String message, BufferedImage screenshot) {
		this.id = id;
		this.message = message;
		this.screenshot = screenshot;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public BufferedImage getScreenshot() {
		return screenshot;
	}

	
	
}
