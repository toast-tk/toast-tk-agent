package com.synaptix.toast.plugin.synaptix.runtime.model;

import com.synaptix.swing.DayDate;

public final class CellTarget {

	public final int ressource;

	public final DayDate dayMin;

	public final DayDate dayMax;

	public CellTarget(
			final int ressource,
			final DayDate dayMin,
			final DayDate dayMax
	) {
		this.ressource = ressource;
		this.dayMin = dayMin;
		this.dayMax = dayMax;
	}
}