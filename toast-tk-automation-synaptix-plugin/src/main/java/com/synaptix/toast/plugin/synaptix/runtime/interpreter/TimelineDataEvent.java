package com.synaptix.toast.plugin.synaptix.runtime.interpreter;

public final class TimelineDataEvent {

	public final int mouseEventID;

	public final int clickCount;

	public final int modifiers;

	public final String classTaskName;//locator base

	public final int dayMin;

	public final int hourMin;

	public final int minMin;

	public final int dayMax;

	public final int hourMax;

	public final int minMax;

	public final int ordre;

	public final int ressource;

	public final String timelineName;//locator add

	public final String ressourceName;

	public String alias;

	public TimelineDataEvent(
			final int mouseEventID,
			final int clickCount,
			final int modifiers,
			final String classTaskName,
			final int dayMin,
			final int hourMin,
			final int minMin,
			final int dayMax,
			final int hourMax,
			final int minMax,
			final int ordre,
			final int ressource,
			final String timelineName,
			final String ressourceName
	) {
		this.mouseEventID = mouseEventID;
		this.clickCount = clickCount;
		this.modifiers = modifiers;
		this.classTaskName = classTaskName;
		this.dayMin = dayMin;
		this.hourMin = hourMin;
		this.minMin = minMin;
		this.dayMax = dayMax;
		this.hourMax = hourMax;
		this.minMax = minMax;
		this.ordre = ordre;
		this.ressource = ressource;
		this.timelineName = timelineName;
		this.ressourceName = ressourceName;
	}

	public String getContainer() {
		return timelineName;
	}

	public String getType() {
		return "type";
	}

	public String getLocator() {
		return new StringBuilder(classTaskName)
		.append(':').append(timelineName).toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb
		.append("mouseEventID : ").append(mouseEventID).append('\n')
		.append("clickCount : ").append(clickCount).append('\n')
		.append("modifiers : ").append(modifiers).append('\n')
		.append("classTaskName : ").append(classTaskName).append('\n')
		.append("dayMin : ").append(dayMin).append('\n')
		.append("hourMin : ").append(hourMin).append('\n')
		.append("minMin : ").append(minMin).append('\n')
		.append("dayMax : ").append(dayMax).append('\n')
		.append("hourMax : ").append(hourMax).append('\n')
		.append("minMax : ").append(minMax).append('\n')
		.append("ordre : ").append(ordre).append('\n')
		.append("ressource : ").append(ressource).append('\n')
		.append("timelineName : ").append(timelineName).append('\n')
		.append("ressourceName : ").append(ressourceName).append('\n')
		;
		
		return sb.toString();
	}
}