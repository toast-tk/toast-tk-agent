package com.synaptix.toast.gwt.client.bean;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PageInfoDto implements IsSerializable {

	public int numElements;
	public String fullName;
	public String shortName;
	public String filePath;
	public List<ElementInfoDto> elements;
	private String dirPath;
	private String fileName;

	public PageInfoDto() {

	}

	public String getName() {
		return fullName;
	}

	public void setName(String name) {
		this.fullName = name;
	}

	public int getNumElements() {
		return numElements;
	}

	public void setNumElements(int numElements) {
		this.numElements = numElements;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public List<ElementInfoDto> getElements() {
		return elements;
	}

	public void setElements(List<ElementInfoDto> elements) {
		this.elements = elements;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
