package com.synaptix.toast.plugin.synaptix.runtime.model;

public class TaskOnRessource {

	public final /*SimpleDaysTask*/Object simpleDaysTask;

	public final int ressource;

	public final String identifier;

	public TaskOnRessource(
			final /*SimpleDaysTask*/Object simpleDaysTask,
			final int ressource,
			final String identifier
	) {
		this.simpleDaysTask = simpleDaysTask;
		this.ressource = ressource;
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb
		.append(" simpleDaysTask = ").append(simpleDaysTask).append('\n')
		.append(" ressource = ").append(ressource).append('\n')
		.append(" identifier = ").append(identifier).append('\n')
		;
		
		return sb.toString();
	}
}