package com.synaptix.toast.core.dao;

public interface IBlock extends ITaggable {

	String getBlockType();

	/**
	 * Number of lines in the text file.
	 */
	int numberOfLines();
}
