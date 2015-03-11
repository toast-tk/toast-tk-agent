package com.synaptix.toast.gwt.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProjectInfoDto implements IsSerializable {

	public String name;
	public String version;
	public String interation;
	private String totalExecutionTime;
	private String executedOn;

	public ProjectInfoDto() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInteration() {
		return interation;
	}

	public void setInteration(String interation) {
		this.interation = interation;
	}

	public String getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public void setTotalExecutionTime(String totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}

	public String getExecutedOn() {
		return executedOn;
	}

	public void setExecutedOn(String executedOn) {
		this.executedOn = executedOn;
	}

}