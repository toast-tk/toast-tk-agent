package com.synaptix.toast.core.dao;

public interface IBlock extends ITaggable {

	String getBlockType();

	/**
	 * Number of lines in the text file.
	 */
	int getNumberOfLines();

	/**
	 * Number of lines to shift the parser reading.
	 */
	int getOffset();
}
