package com.synaptix.toast.plugin.synaptix.runtime.handler;

import com.synaptix.swing.DayDate;

public class ActionTimelineInfo {

	public final String action;

	public final String taskType;

	public final DayDate dayDateMin;

	public final DayDate dayDateMax;

	public final String container;

	public final String ressourceName;

	public final String quantifier;

	public int findedRessource;

	public ActionTimelineInfo(
			final String actionFinded,
			final String taskType,
			final DayDate dayDateMin,
			final DayDate dayDateMax,
			final String name,
			final String ressourceName,
			final String quantifier
	) {
		this.action = actionFinded;
		this.taskType = taskType;
		this.dayDateMin = dayDateMin;
		this.dayDateMax = dayDateMax;
		this.container = name;
		this.ressourceName = ressourceName;
		this.quantifier = quantifier;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb
		.append(action)
		.append(" sur ")
		.append(taskType)
		.append(" du ")
		.append(dayDateMin)
		.append(" au ")
		.append(dayDateMax)
		.append(" du ")
		.append(container)
		.append(" de ")
		.append(ressourceName)
		.append(" ")
		.append(quantifier)
		;
		return sb.toString();
	}
}