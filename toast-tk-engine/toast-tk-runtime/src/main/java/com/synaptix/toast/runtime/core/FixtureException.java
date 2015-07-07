package com.synaptix.toast.runtime.core;

public class FixtureException extends RuntimeException {

	private static final long serialVersionUID = 5995934095306036344L;

	public FixtureException() {
		super();
	}

	public FixtureException(
		String message,
		Throwable cause) {
		super(message, cause);
	}

	public FixtureException(
		String message) {
		super(message);
	}

	public FixtureException(
		Throwable cause) {
		super(cause);
	}

	public FixtureException(
		Object fixture,
		String message) {
		super("Fixture : " + fixture.getClass() + " has raised an exception : " + message);
	}

	public FixtureException(
		Object fixture,
		Throwable cause) {
		super("Fixture : " + fixture.getClass(), cause);
	}
}
